/*
 * Copyright (C) 2010 Ken Ellinwood
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* This file is a heavily modified version of com.android.signapk.SignApk.java.
 * The changes include:
 *   - addition of the signZip() convenience methods
 *   - addition of a progress listener interface
 *   - removal of main()
 *   - switch to a signature generation method that verifies
 *     in Android recovery
 *   - eliminated dependency on sun.security and sun.misc APIs by 
 *     using signature block template files.
 */

package com.oddlyspaced.prjkt.utils.kellinwood.zipsigner;

import android.content.Context;
import android.os.Environment;


import com.oddlyspaced.prjkt.utils.kellinwood.logging.LoggerInterface;
import com.oddlyspaced.prjkt.utils.kellinwood.logging.LoggerManager;
import com.oddlyspaced.prjkt.utils.kellinwood.optional.SignatureBlockGenerator;
import com.oddlyspaced.prjkt.utils.kellinwood.zipio.ZioEntry;
import com.oddlyspaced.prjkt.utils.kellinwood.zipio.ZipInput;
import com.oddlyspaced.prjkt.utils.kellinwood.zipio.ZipOutput;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.security.DigestOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This is a modified copy of com.android.signapk.SignApk.java.  It provides an
 * API to sign JAR files (including APKs and Zip/OTA updates) in
 * a way compatible with the mincrypt verifier, using SHA1 and RSA keys.
 *
 * Please see the README.txt file in the root of this project for usage instructions.
 */
public class ZipSigner 
{

    public Context context;

    private boolean canceled = false;

    private ProgressHelper progressHelper = new ProgressHelper();
    private ResourceAdapter resourceAdapter = new DefaultResourceAdapter();
    
    static LoggerInterface log = null;

    private static final String CERT_SF_NAME = "META-INF/CERT.SF";
    private static final String CERT_RSA_NAME = "META-INF/CERT.RSA";

    // Files matching this pattern are not copied to the output.
    private static Pattern stripPattern =
        Pattern.compile("^META-INF/(.*)[.](SF|RSA|DSA)$");

    Map<String,KeySet> loadedKeys = new HashMap<String,KeySet>();
    KeySet keySet = null;
    
    public static LoggerInterface getLogger() {
        if (log == null) log = LoggerManager.getLogger( ZipSigner.class.getName());
        return log;
    }

    public static final String MODE_AUTO_TESTKEY = "auto-testkey";
    public static final String MODE_AUTO_NONE = "auto-none";
    public static final String MODE_AUTO = "auto";
    public static final String KEY_NONE = "none";
    public static final String KEY_TESTKEY = "testkey";
    
    // Allowable key modes.
    public static final String[] SUPPORTED_KEY_MODES =
        new String[] { MODE_AUTO_TESTKEY, MODE_AUTO, MODE_AUTO_NONE, "media", "platform", "shared", KEY_TESTKEY, KEY_NONE};
    
    String keymode = KEY_TESTKEY; // backwards compatible with versions that only signed with this key
    
    Map<String,String> autoKeyDetect = new HashMap<String,String>();
    
    AutoKeyObservable autoKeyObservable = new AutoKeyObservable();
    
    public ZipSigner() throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        // MD5 of the first 1458 bytes of the signature block generated by the key, mapped to the key name
        autoKeyDetect.put( "aa9852bc5a53272ac8031d49b65e4b0e", "media");
        autoKeyDetect.put( "e60418c4b638f20d0721e115674ca11f", "platform");
        autoKeyDetect.put( "3e24e49741b60c215c010dc6048fca7d", "shared");
        autoKeyDetect.put( "dab2cead827ef5313f28e22b6fa8479f", "testkey");
        
    }

    public ResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    public void setResourceAdapter(ResourceAdapter resourceAdapter) {
        this.resourceAdapter = resourceAdapter;
    }

    // when the key mode is automatic, the observers are called when the key is determined
    public void addAutoKeyObserver( Observer o) {
        autoKeyObservable.addObserver(o);
    }
    
    public String getKeymode() {
        return keymode;
    }

    public void setKeymode(String km) throws IOException, GeneralSecurityException  
    {
        if (getLogger().isDebugEnabled()) getLogger().debug("setKeymode: " + km);
        keymode = km;
        if (keymode.startsWith(MODE_AUTO)) {
            keySet = null;
        }
        else {
            progressHelper.initProgress();        
            loadKeys( keymode);
        }
    }

    public static String[] getSupportedKeyModes() {
        return SUPPORTED_KEY_MODES;
    }

    
    protected String autoDetectKey( String mode, Map<String, ZioEntry> zioEntries)
        throws NoSuchAlgorithmException, IOException 
    {
        boolean debug = getLogger().isDebugEnabled();
        
        if (!mode.startsWith(MODE_AUTO)) return mode;
        

        // Auto-determine which keys to use
        String keyName = null;
        // Start by finding the signature block file in the input.
        for (Map.Entry<String,ZioEntry> entry : zioEntries.entrySet()) {
            String entryName = entry.getKey();
            if (entryName.startsWith("META-INF/") && entryName.endsWith(".RSA")) {
                
                // Compute MD5 of the first 1458 bytes, which is the size of our signature block templates -- 
                // e.g., the portion of the sig block file that is the same for a given certificate.                    
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] entryData = entry.getValue().getData();
                if (entryData.length < 1458) break; // sig block too short to be a supported key 
                md5.update( entryData, 0, 1458);
                byte[] rawDigest = md5.digest();
                
                // Create the hex representation of the digest value
                StringBuilder builder = new StringBuilder();
                for( byte b : rawDigest) {
                    builder.append( String.format("%02x", b));
                }
                
                String md5String = builder.toString();
                // Lookup the key name
                keyName = autoKeyDetect.get( md5String); 
                
                
                if (debug) {
                    if (keyName != null) {
                        getLogger().debug(String.format("Auto-determined key=%s using md5=%s", keyName, md5String));
                    } else {
                        getLogger().debug(String.format("Auto key determination failed for md5=%s", md5String));
                    }
                }
                if (keyName != null) return keyName;
            }
        }
        
        if (mode.equals( MODE_AUTO_TESTKEY)) {
            // in auto-testkey mode, fallback to the testkey if it couldn't be determined
            if (debug) getLogger().debug("Falling back to key="+ keyName);
            return KEY_TESTKEY;
            
        }
        else if (mode.equals(MODE_AUTO_NONE)) {
            // in auto-node mode, simply copy the input to the output when the key can't be determined.
            if (debug) getLogger().debug("Unable to determine key, returning: " + KEY_NONE);
            return KEY_NONE;
        }
        
        return null;
    }

    public void issueLoadingCertAndKeysProgressEvent() {
        progressHelper.progress(ProgressEvent.PRORITY_IMPORTANT, resourceAdapter.getString(ResourceAdapter.Item.LOADING_CERTIFICATE_AND_KEY));
    }

    // Loads one of the built-in keys (media, platform, shared, testkey)
    public void loadKeys( String name)
        throws IOException, GeneralSecurityException
    {
        
        keySet = loadedKeys.get(name);
        if (keySet != null) return;
        
        keySet = new KeySet();
        keySet.setName(name);
        loadedKeys.put( name, keySet);
        
        if (KEY_NONE.equals(name)) return;

        issueLoadingCertAndKeysProgressEvent();

        // load the private key
        File privateKey = new File(Environment.getExternalStorageDirectory().getPath() + "/keys/"+name+".pk8");

        // URL privateKeyUrl = getClass().getResource("/keys/"+name+".pk8");
        URL privateKeyUrl = privateKey.toURL();
        // ^ this is null

        keySet.setPrivateKey(readPrivateKey(privateKeyUrl, null));

        // load the certificate
        File publicKey = new File(Environment.getExternalStorageDirectory().getPath() + "/keys/"+name+".x509.pem");

        // URL publicKeyUrl = getClass().getResource("/keys/"+name+".x509.pem");
        URL publicKeyUrl = publicKey.toURL();

        keySet.setPublicKey(readPublicKey(publicKeyUrl));

        // load the signature block template
        URL sigBlockTemplateUrl = getClass().getResource("/keys/"+name+".sbt");
        if (sigBlockTemplateUrl != null) {
            keySet.setSigBlockTemplate(readContentAsBytes(sigBlockTemplateUrl));
        }
    }
    
    public void setKeys( String name, X509Certificate publicKey, PrivateKey privateKey, byte[] signatureBlockTemplate)
    {
        keySet = new KeySet( name, publicKey, privateKey, signatureBlockTemplate);
    }

    public void setKeys( String name, X509Certificate publicKey, PrivateKey privateKey, String signatureAlgorithm, byte[] signatureBlockTemplate)
    {
        keySet = new KeySet( name, publicKey, privateKey, signatureAlgorithm, signatureBlockTemplate);
    }

    public KeySet getKeySet() {
        return keySet;
    }
    
    // Allow the operation to be canceled.
    public void cancel() {
        canceled = true;
    }
    
    // Allow the instance to sign again if previously canceled.
    public void resetCanceled() {
        canceled = false;
    }

    public boolean isCanceled() {
        return canceled;
    }

    @SuppressWarnings("unchecked")
    public void loadProvider( String providerClassName)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class providerClass = Class.forName(providerClassName);
        Provider provider = (Provider)providerClass.newInstance();
        Security.insertProviderAt(provider, 1);
    }


    public X509Certificate readPublicKey(URL publicKeyUrl)
    throws IOException, GeneralSecurityException {
        InputStream input = publicKeyUrl.openStream();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(input);
        } finally {
            input.close();
        }
    }

    /**
     * Decrypt an encrypted PKCS 8 format private key.
     *
     * Based on ghstark's post on Aug 6, 2006 at
     * http://forums.sun.com/thread.jspa?threadID=758133&messageID=4330949
     *
     * @param encryptedPrivateKey The raw data of the private key
     * @param keyPassword the key password
     */
    private KeySpec decryptPrivateKey(byte[] encryptedPrivateKey, String keyPassword)
    throws GeneralSecurityException {
        EncryptedPrivateKeyInfo epkInfo;
        try {
            epkInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKey);
        } catch (IOException ex) {
            // Probably not an encrypted key.
            return null;
        }

        char[] keyPasswd = keyPassword.toCharArray();

        SecretKeyFactory skFactory = SecretKeyFactory.getInstance(epkInfo.getAlgName());
        Key key = skFactory.generateSecret(new PBEKeySpec(keyPasswd));

        Cipher cipher = Cipher.getInstance(epkInfo.getAlgName());
        cipher.init(Cipher.DECRYPT_MODE, key, epkInfo.getAlgParameters());

        try {
            return epkInfo.getKeySpec(cipher);
        } catch (InvalidKeySpecException ex) {
            getLogger().error("signapk: Password for private key may be bad.");
            throw ex;
        }
    }

    /** Fetch the content at the specified URL and return it as a byte array. */
    public byte[] readContentAsBytes( URL contentUrl) throws IOException
    {
        return readContentAsBytes( contentUrl.openStream());
    }

    /** Fetch the content from the given stream and return it as a byte array. */
    public byte[] readContentAsBytes( InputStream input) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[2048];

        int numRead = input.read( buffer);
        while (numRead != -1) {
            baos.write( buffer, 0, numRead);
            numRead = input.read( buffer);
        }

        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /** Read a PKCS 8 format private key. */
    public PrivateKey readPrivateKey(URL privateKeyUrl, String keyPassword)
    throws IOException, GeneralSecurityException {

        DataInputStream input = new DataInputStream( privateKeyUrl.openStream());
        try {
            byte[] bytes = readContentAsBytes( input);

            KeySpec spec = decryptPrivateKey(bytes, keyPassword);
            if (spec == null) {
                spec = new PKCS8EncodedKeySpec(bytes);
            }

            try {
                return KeyFactory.getInstance("RSA").generatePrivate(spec);
            } catch (InvalidKeySpecException ex) {
                return KeyFactory.getInstance("DSA").generatePrivate(spec);
            }
        } finally {
            input.close();
        }
    }

    /** Add the SHA1 of every file to the manifest, creating it if necessary. */
    private Manifest addDigestsToManifest(Map<String,ZioEntry> entries)
        throws IOException, GeneralSecurityException 
    {
        Manifest input = null;
        ZioEntry manifestEntry = entries.get(JarFile.MANIFEST_NAME);
        if (manifestEntry != null) {
            input = new Manifest();
            input.read( manifestEntry.getInputStream());
        }
        Manifest output = new Manifest();
        Attributes main = output.getMainAttributes();
        if (input != null) {
            main.putAll(input.getMainAttributes());
        } else {
            main.putValue("Manifest-Version", "1.0");
            main.putValue("Created-By", "1.0 (Android SignApk)");
        }

        // BASE64Encoder base64 = new BASE64Encoder();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] buffer = new byte[512];
        int num;

        // We sort the input entries by name, and add them to the
        // output manifest in sorted order.  We expect that the output
        // map will be deterministic.

        TreeMap<String, ZioEntry> byName = new TreeMap<String, ZioEntry>();
        byName.putAll( entries);

        boolean debug = getLogger().isDebugEnabled();
        if (debug) getLogger().debug("Manifest entries:");
        for (ZioEntry entry: byName.values()) {
            if (canceled) break;
            String name = entry.getName();
            if (debug) getLogger().debug(name);
            if (!entry.isDirectory() && !name.equals(JarFile.MANIFEST_NAME) &&
                    !name.equals(CERT_SF_NAME) && !name.equals(CERT_RSA_NAME) &&
                    (stripPattern == null ||
                     !stripPattern.matcher(name).matches()))
            {

                progressHelper.progress( ProgressEvent.PRORITY_NORMAL, resourceAdapter.getString(ResourceAdapter.Item.GENERATING_MANIFEST));
                InputStream data = entry.getInputStream();
                while ((num = data.read(buffer)) > 0) {
                    md.update(buffer, 0, num);
                }
                    
                Attributes attr = null;
                if (input != null) {
                    Attributes inAttr = input.getAttributes(name);
                    if (inAttr != null) attr = new Attributes( inAttr);
                }
                if (attr == null) attr = new Attributes();
                attr.putValue("SHA1-Digest", Base64.encode(md.digest()));
                output.getEntries().put(name, attr);
            }
        }

        return output;
    }


    /** Write the signature file to the given output stream. */
    private void generateSignatureFile(Manifest manifest, OutputStream out)
    throws IOException, GeneralSecurityException {
        out.write( ("Signature-Version: 1.0\r\n").getBytes());
        out.write( ("Created-By: 1.0 (Android SignApk)\r\n").getBytes());


        // BASE64Encoder base64 = new BASE64Encoder();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        PrintStream print = new PrintStream(
                new DigestOutputStream(new ByteArrayOutputStream(), md),
                true, "UTF-8");

        // Digest of the entire manifest
        manifest.write(print);
        print.flush();

        out.write( ("SHA1-Digest-Manifest: "+ Base64.encode(md.digest()) + "\r\n\r\n").getBytes());

        Map<String, Attributes> entries = manifest.getEntries();
        for (Map.Entry<String, Attributes> entry : entries.entrySet()) {
            if (canceled) break;
            progressHelper.progress( ProgressEvent.PRORITY_NORMAL, resourceAdapter.getString(ResourceAdapter.Item.GENERATING_SIGNATURE_FILE));
            // Digest of the manifest stanza for this entry.
            String nameEntry = "Name: " + entry.getKey() + "\r\n"; 
            print.print( nameEntry);
            for (Map.Entry<Object, Object> att : entry.getValue().entrySet()) {
                print.print(att.getKey() + ": " + att.getValue() + "\r\n");
            }
            print.print("\r\n");
            print.flush();

            out.write( nameEntry.getBytes());
            out.write( ("SHA1-Digest: " +  Base64.encode(md.digest()) + "\r\n\r\n").getBytes());
        }

    }

    /** Write a .RSA file with a digital signature. */
    @SuppressWarnings("unchecked")
    private void writeSignatureBlock( KeySet keySet, byte[] signatureFileBytes, OutputStream out)
        throws IOException, GeneralSecurityException
    {
        if (keySet.getSigBlockTemplate() != null) {

            // Can't use default Signature on Android.  Although it generates a signature that can be verified by jarsigner,
            // the recovery program appears to require a specific algorithm/mode/padding.  So we use the custom ZipSignature instead.
            // Signature signature = Signature.getInstance("SHA1withRSA");
            ZipSignature signature = new ZipSignature();
            signature.initSign(keySet.getPrivateKey());
            signature.update(signatureFileBytes);
            byte[] signatureBytes = signature.sign();

            out.write( keySet.getSigBlockTemplate());
            out.write( signatureBytes);

            if (getLogger().isDebugEnabled()) {

                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update( signatureFileBytes);
                byte[] sfDigest = md.digest();
                getLogger().debug( "Sig File SHA1: \n" + HexDumpEncoder.encode( sfDigest));

                getLogger().debug( "Signature: \n" + HexDumpEncoder.encode(signatureBytes));

                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySet.getPublicKey());

                byte[] tmpData = cipher.doFinal( signatureBytes);
                getLogger().debug( "Signature Decrypted: \n" + HexDumpEncoder.encode(tmpData));
            }
        }
        else {
            try {
                byte[] sigBlock = null;
                // Use reflection to call the optional generator.
                // Method generatorMethod = generatorClass.getMethod("generate", KeySet.class, (new byte[1]).getClass());
                // sigBlock = (byte[])generatorMethod.invoke(null, keySet, signatureFileBytes);
                sigBlock = SignatureBlockGenerator.generate(keySet, signatureFileBytes);
                out.write(sigBlock);
            } catch (Exception x) {
                x.printStackTrace();
                // throw new RuntimeException(x.getMessage(),x);
            }
        }
    }

    /**
     * Copy all the files in a manifest from input to output.  We set
     * the modification times in the output to a fixed time, so as to
     * reduce variation in the output file and make incremental OTAs
     * more efficient.
     */
    private void copyFiles(Manifest manifest, Map<String,ZioEntry> input, ZipOutput output, long timestamp)
        throws IOException 
    {
        Map<String, Attributes> entries = manifest.getEntries();
        List<String> names = new ArrayList<String>(entries.keySet());
        Collections.sort(names);
        int i = 1;
        for (String name : names) {
            if (canceled) break;
            progressHelper.progress(ProgressEvent.PRORITY_NORMAL, resourceAdapter.getString(ResourceAdapter.Item.COPYING_ZIP_ENTRY, i, names.size()));
            i += 1;
            ZioEntry inEntry = input.get(name);
            inEntry.setTime(timestamp);
            output.write(inEntry);

        }
    }

    /**
     * Copy all the files from input to output. 
     */
    private void copyFiles(Map<String,ZioEntry> input, ZipOutput output)
        throws IOException 
    {
        int i = 1;
        for (ZioEntry inEntry : input.values()) {
            if (canceled) break;
            progressHelper.progress( ProgressEvent.PRORITY_NORMAL, resourceAdapter.getString(ResourceAdapter.Item.COPYING_ZIP_ENTRY, i, input.size()));
            i += 1;
            output.write(inEntry);
        }
    }

    /**
     * @deprecated - use the version that takes the passwords as char[]
     */
    public void signZip( URL keystoreURL,
                         String keystoreType,
                         String keystorePw,
                         String certAlias,
                         String certPw,
                         String inputZipFilename,
                         String outputZipFilename)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException,
        IOException, GeneralSecurityException
    {
        signZip( keystoreURL, keystoreType, keystorePw.toCharArray(), certAlias, certPw.toCharArray(), "SHA1withRSA", inputZipFilename, outputZipFilename);
    }

    public void signZip( URL keystoreURL, 
            String keystoreType,
            char[] keystorePw,
            String certAlias,
            char[] certPw,
            String signatureAlgorithm,
            String inputZipFilename, 
            String outputZipFilename)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException,
        IOException, GeneralSecurityException
    {
        InputStream keystoreStream = null;


        try {
            KeyStore keystore = null;
            if (keystoreType == null) keystoreType = KeyStore.getDefaultType();
            keystore = KeyStore.getInstance(keystoreType);

            keystoreStream = keystoreURL.openStream();
            keystore.load(keystoreStream, keystorePw);
            Certificate cert = keystore.getCertificate(certAlias);
            X509Certificate publicKey = (X509Certificate)cert;
            Key key = keystore.getKey(certAlias, certPw);
            PrivateKey privateKey = (PrivateKey)key;
            
            setKeys( "custom", publicKey, privateKey, signatureAlgorithm, null);

            signZip( inputZipFilename, outputZipFilename);
        }
        finally {
            if (keystoreStream != null) keystoreStream.close();
        }
    }


    

    /** Sign the input with the default test key and certificate.  
     *  Save result to output file.
     */
    public void signZip( Map<String,ZioEntry> zioEntries, String outputZipFilename)
        throws IOException, GeneralSecurityException
    {
        progressHelper.initProgress();        
        signZip( zioEntries, new FileOutputStream(outputZipFilename), outputZipFilename);
    }
    

    /** Sign the file using the given public key cert, private key,
     *  and signature block template.  The signature block template
     *  parameter may be null, but if so
     *  android-sun-jarsign-support.jar must be in the classpath.
     */
    public void signZip( String inputZipFilename, String outputZipFilename)
        throws IOException, GeneralSecurityException
    {
        File inFile = new File( inputZipFilename).getCanonicalFile();
        File outFile = new File( outputZipFilename).getCanonicalFile();
        
        if (inFile.equals(outFile)) {
            throw new IllegalArgumentException( resourceAdapter.getString(ResourceAdapter.Item.INPUT_SAME_AS_OUTPUT_ERROR));
        }        

        progressHelper.initProgress();        
        progressHelper.progress( ProgressEvent.PRORITY_IMPORTANT, resourceAdapter.getString(ResourceAdapter.Item.PARSING_CENTRAL_DIRECTORY));
        
        ZipInput input = ZipInput.read( inputZipFilename);
        signZip( input.getEntries(), new FileOutputStream( outputZipFilename), outputZipFilename);
    }
    
    /** Sign the 
     *  and signature block template.  The signature block template
     *  parameter may be null, but if so
     *  android-sun-jarsign-support.jar must be in the classpath.
     */
    public void signZip( Map<String,ZioEntry> zioEntries, OutputStream outputStream, String outputZipFilename)
        throws IOException, GeneralSecurityException    
    {
        boolean debug =  getLogger().isDebugEnabled();
        
        progressHelper.initProgress();
        if (keySet == null) {
            if (!keymode.startsWith(MODE_AUTO)) 
                throw new IllegalStateException("No keys configured for signing the file!");
            
            // Auto-determine which keys to use
            String keyName = this.autoDetectKey( keymode, zioEntries);
            if (keyName == null) 
                throw new AutoKeyException( resourceAdapter.getString(ResourceAdapter.Item.AUTO_KEY_SELECTION_ERROR, new File( outputZipFilename).getName()));
            
            autoKeyObservable.notifyObservers(keyName);

            loadKeys( keyName);
            
        }


        
        ZipOutput zipOutput = null;

        try {


            zipOutput = new ZipOutput( outputStream);

            if (KEY_NONE.equals(keySet.getName())) {
                progressHelper.setProgressTotalItems(zioEntries.size());
                progressHelper.setProgressCurrentItem(0);                
                copyFiles(zioEntries, zipOutput);
                return;
            }
            
            // Calculate total steps to complete for accurate progress percentages.
            int progressTotalItems = 0;
            for (ZioEntry entry: zioEntries.values()) {
                String name = entry.getName();
                if (!entry.isDirectory() && !name.equals(JarFile.MANIFEST_NAME) &&
                        !name.equals(CERT_SF_NAME) && !name.equals(CERT_RSA_NAME) &&
                        (stripPattern == null ||
                                !stripPattern.matcher(name).matches()))
                {
                    progressTotalItems += 3;  // digest for manifest, digest in sig file, copy data
                }
            }
            progressTotalItems += 1; // CERT.RSA generation
            progressHelper.setProgressTotalItems(progressTotalItems);
            progressHelper.setProgressCurrentItem(0);

            // Assume the certificate is valid for at least an hour.
            long timestamp = keySet.getPublicKey().getNotBefore().getTime() + 3600L * 1000;
            
            // MANIFEST.MF
            // progress(ProgressEvent.PRORITY_NORMAL, JarFile.MANIFEST_NAME);
            Manifest manifest = addDigestsToManifest(zioEntries);
            if (canceled) return;
            ZioEntry ze = new ZioEntry( JarFile.MANIFEST_NAME);
            ze.setTime(timestamp);
            manifest.write(ze.getOutputStream());
            zipOutput.write(ze);


            // CERT.SF
            ze = new ZioEntry(CERT_SF_NAME);
            ze.setTime(timestamp);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            generateSignatureFile(manifest, out);
            if (canceled) return;
            byte[] sfBytes = out.toByteArray();
            if (debug) {
                getLogger().debug( "Signature File: \n" + new String( sfBytes) + "\n" + 
                        HexDumpEncoder.encode( sfBytes));
            }
            ze.getOutputStream().write(sfBytes);
            zipOutput.write(ze);

            // CERT.RSA
            progressHelper.progress( ProgressEvent.PRORITY_NORMAL, resourceAdapter.getString(ResourceAdapter.Item.GENERATING_SIGNATURE_BLOCK));
            ze = new ZioEntry(CERT_RSA_NAME);
            ze.setTime(timestamp);
            writeSignatureBlock(keySet, sfBytes, ze.getOutputStream());
            zipOutput.write( ze);
            if (canceled) return;

            // Everything else
            copyFiles(manifest, zioEntries, zipOutput, timestamp);
            if (canceled) return;
            
        }
        finally {
            zipOutput.close();
            if (canceled) {
                try {
                    if (outputZipFilename != null) new File( outputZipFilename).delete();
                }
                catch (Throwable t) {
                    getLogger().warning( t.getClass().getName() + ":" + t.getMessage());
                }
            }
        }
    }
    
    public void addProgressListener( ProgressListener l)
    {
        progressHelper.addProgressListener(l);
    }

    public synchronized void removeProgressListener( ProgressListener l)
    {
        progressHelper.removeProgressListener(l);
    }     

    
    public static class AutoKeyObservable extends Observable
    {
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }
        
    }
}
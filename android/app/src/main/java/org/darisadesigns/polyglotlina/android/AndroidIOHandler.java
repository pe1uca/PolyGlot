package org.darisadesigns.polyglotlina.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.darisadesigns.polyglotlina.CustHandler;
import org.darisadesigns.polyglotlina.CustHandlerFactory;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.IOHandler;
import org.darisadesigns.polyglotlina.ManagersCollections.GrammarManager;
import org.darisadesigns.polyglotlina.ManagersCollections.ImageCollection;
import org.darisadesigns.polyglotlina.ManagersCollections.LogoCollection;
import org.darisadesigns.polyglotlina.ManagersCollections.ReversionManager;
import org.darisadesigns.polyglotlina.Nodes.ImageNode;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

public class AndroidIOHandler implements IOHandler {
    private static final String TAG = "IOHandler";

    private final Context context;

    public AndroidIOHandler(Context context) {
        this.context = context;
    }

    private void missingImplementation() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();

        Log.e(TAG, "Missing implementation: " + stackTrace[1].getMethodName());
    }

    @Override
    public File createTmpFileWithContents(String contents, String extension) throws IOException {
        missingImplementation();
        return null;
    }

    @Override
    public File createTmpFileFromImageBytes(byte[] imageBytes, String fileName) throws IOException {
        File tmpFile = File.createTempFile(fileName, ".png");
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        OutputStream outStream = new FileOutputStream(tmpFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 85, outStream);
        outStream.close();
        return tmpFile;
    }

    @Override
    public File createFileWithContents(String path, String contents) throws IOException {
        missingImplementation();
        return null;
    }

    @Override
    public byte[] getByteArrayFromFile(File file) throws IOException {
        missingImplementation();
        return new byte[0];
    }

    @Override
    public byte[] streamToByetArray(InputStream is) throws IOException {
        missingImplementation();
        return new byte[0];
    }

    @Override
    public byte[] getFileByteArray(String filePath) throws IOException {
        missingImplementation();
        return new byte[0];
    }

    @Override
    public CustHandler getHandlerFromFile(String _fileName, DictCore _core) throws IOException {
        CustHandler ret = null;

        if (isFileZipArchive(_fileName)) {
            try (ZipFile zipFile = new ZipFile(_fileName)) {
                ZipEntry xmlEntry = zipFile.getEntry(PGTUtil.LANG_FILE_NAME);
                try (InputStream ioStream = zipFile.getInputStream(xmlEntry)) {
                    ret = CustHandlerFactory.getCustHandler(ioStream, _core);
                }
                catch (Exception e) {
                    throw new IOException(e.getLocalizedMessage(), e);
                }
            }
        } else {
            try (InputStream ioStream = new FileInputStream(_fileName)) {
                ret = CustHandlerFactory.getCustHandler(ioStream, _core);
            }
            catch (Exception e) {
                throw new IOException(e.getLocalizedMessage(), e);
            }
        }

        return ret;
    }

    @Override
    public CustHandler getHandlerFromByteArray(byte[] byteArray, DictCore _core) throws IOException {
        try {
            return CustHandlerFactory.getCustHandler(new ByteArrayInputStream(byteArray), _core);
        }
        catch (Exception e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String getFilenameFromPath(String fullPath) {
        missingImplementation();
        return null;
    }

    @Override
    public void deleteIni(String workingDirectory) {
        missingImplementation();
    }

    @Override
    public void parseHandler(String _fileName, CustHandler _handler) throws IOException, ParserConfigurationException, SAXException {
        try (ZipFile zipFile = new ZipFile(_fileName)) {
            ZipEntry xmlEntry = zipFile.getEntry(PGTUtil.LANG_FILE_NAME);
            try (InputStream ioStream = zipFile.getInputStream(xmlEntry)) {
                parseHandlerInternal(ioStream, _handler);
            }
        }
    }

    @Override
    public void parseHandlerByteArray(byte[] reversion, CustHandler _handler) throws ParserConfigurationException, IOException, SAXException {
        parseHandlerInternal(new ByteArrayInputStream(reversion), _handler);
    }

    private void parseHandlerInternal(InputStream stream, CustHandler _handler)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(stream, _handler);
    }

    @Override
    public boolean isFileZipArchive(String _fileName) throws IOException {
        File file = new File(_fileName);

        // ignore directories and files too small to possibly be archives
        if (file.isDirectory()
                || file.length() < 4) {
            return false;
        }

        int test;
        try (FileInputStream fileStream = new FileInputStream(file)) {
            try (BufferedInputStream buffer = new BufferedInputStream(fileStream)) {
                try (DataInputStream in = new DataInputStream(buffer)) {
                    test = in.readInt();
                }
            }
        }
        return test == 0x504b0304;
    }

    @Override
    public void writeFile(String _fileName, Document doc, DictCore core, File workingDirectory, Instant saveTime) throws IOException, TransformerException {
        missingImplementation();
    }

    @Override
    public File getTempSaveFileIfExists(File workingDirectory) {
        missingImplementation();
        return null;
    }

    @Override
    public File archiveFile(File source, File workingDirectory) throws IOException {
        missingImplementation();
        return null;
    }

    @Override
    public void copyFile(Path fromLocation, Path toLocation, boolean replaceExisting) throws IOException {
        missingImplementation();
    }

    @Override
    public boolean fileExists(String fullPath) {
        File file = new File(fullPath);
        return file.exists();
    }

    @Override
    public void loadImageAssets(ImageCollection imageCollection, String fileName) throws Exception {
        try (ZipFile zipFile = new ZipFile(fileName)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry entry;
            while (entries.hasMoreElements()) { // find images directory (zip paths are linear, only simulating tree structure)
                entry = entries.nextElement();
                if (!entry.getName().equals(PGTUtil.IMAGES_SAVE_PATH)) {
                    continue;
                }
                break;
            }

            while (entries.hasMoreElements()) {
                entry = entries.nextElement();

                if (entry.isDirectory()) { // kills process after last image found
                    break;
                }

                try (InputStream imageStream = zipFile.getInputStream(entry)) {
                    String name = entry.getName().replace(".png", "")
                            .replace(PGTUtil.IMAGES_SAVE_PATH, "");
                    int imageId = Integer.parseInt(name);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    ImageNode imageNode = new ImageNode(((PolyGlot)context).getCore());
                    imageNode.setId(imageId);
                    imageNode.setImageBytes(
                            loadImageBytesFromBitmap(bitmap)
                    );
                    imageCollection.getBuffer().setEqual(imageNode);
                    imageCollection.insert(imageId);
                }
            }
        }
    }

    private byte[] loadImageBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void loadLogographs(LogoCollection logoCollection, String fileName) throws Exception {
        missingImplementation();
    }

    @Override
    public void loadReversionStates(ReversionManager reversionManager, String fileName) throws IOException {
        missingImplementation();
    }

    @Override
    public void exportFont(String exportPath, String dictionaryPath) throws IOException {
        missingImplementation();
    }

    @Override
    public void exportCharisFont(String exportPath) throws IOException {
        missingImplementation();
    }

    @Override
    public void loadGrammarSounds(String fileName, GrammarManager grammarManager) throws Exception {
        missingImplementation();
    }

    @Override
    public boolean openFileNativeOS(String path) {
        missingImplementation();
        return false;
    }

    @Override
    public File getDirectoryFromPath(String path) {
        missingImplementation();
        return null;
    }

    @Override
    public File getFileFromPath(String path) {
        missingImplementation();
        return null;
    }

    @Override
    public void writeErrorLog(Throwable exception) {
        Log.e(TAG, "Error", exception);
    }

    @Override
    public void writeErrorLog(Throwable exception, String comment) {
        Log.e(TAG, comment, exception);
    }

    @Override
    public File getErrorLogFile() {
        missingImplementation();
        return null;
    }

    @Override
    public String getErrorLog() throws FileNotFoundException {
        missingImplementation();
        return null;
    }

    @Override
    public String getSystemInformation() {
        missingImplementation();
        return null;
    }

    @Override
    public File unzipResourceToTempLocation(String resourceLocation) throws IOException {
        missingImplementation();
        return null;
    }

    @Override
    public void unzipResourceToDir(String internalPath, Path target) throws IOException {
        missingImplementation();
    }

    @Override
    public void addFileAttributeOSX(String filePath, String attribute, String value, boolean isHexVal) throws Exception {
        missingImplementation();
    }

    @Override
    public String getFileAttributeOSX(String filePath, String attribute) throws Exception {
        missingImplementation();
        return null;
    }

    @Override
    public String[] runAtConsole(String[] arguments, boolean addSpaces) {
        missingImplementation();
        return new String[0];
    }

    @Override
    public String getTerminalJavaVersion() {
        missingImplementation();
        return null;
    }

    @Override
    public boolean isJavaAvailableInTerminal() {
        missingImplementation();
        return false;
    }

    @Override
    public byte[] clearCarrigeReturns(byte[] filthyWithWindows) {
        missingImplementation();
        return new byte[0];
    }

    @Override
    public byte[] loadImageBytes(String path) throws IOException {
        missingImplementation();
        return new byte[0];
    }
}

package org.darisadesigns.polyglotlina.android;

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
import org.darisadesigns.polyglotlina.PGTUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

public class AndroidIOHandler implements IOHandler {
    private static final String TAG = "IOHandler";
    @Override
    public File createTmpFileWithContents(String contents, String extension) throws IOException {
        return null;
    }

    @Override
    public File createTmpFileFromImageBytes(byte[] imageBytes, String fileName) throws IOException {
        return null;
    }

    @Override
    public File createFileWithContents(String path, String contents) throws IOException {
        return null;
    }

    @Override
    public byte[] getByteArrayFromFile(File file) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] streamToByetArray(InputStream is) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] getFileByteArray(String filePath) throws IOException {
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
        return null;
    }

    @Override
    public void deleteIni(String workingDirectory) {

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

    }

    @Override
    public File getTempSaveFileIfExists(File workingDirectory) {
        return null;
    }

    @Override
    public File archiveFile(File source, File workingDirectory) throws IOException {
        return null;
    }

    @Override
    public void copyFile(Path fromLocation, Path toLocation, boolean replaceExisting) throws IOException {

    }

    @Override
    public boolean fileExists(String fullPath) {
        File file = new File(fullPath);
        return file.exists();
    }

    @Override
    public void loadImageAssets(ImageCollection imageCollection, String fileName) throws Exception {

    }

    @Override
    public void loadLogographs(LogoCollection logoCollection, String fileName) throws Exception {

    }

    @Override
    public void loadReversionStates(ReversionManager reversionManager, String fileName) throws IOException {

    }

    @Override
    public void exportFont(String exportPath, String dictionaryPath) throws IOException {

    }

    @Override
    public void exportCharisFont(String exportPath) throws IOException {

    }

    @Override
    public void loadGrammarSounds(String fileName, GrammarManager grammarManager) throws Exception {

    }

    @Override
    public boolean openFileNativeOS(String path) {
        return false;
    }

    @Override
    public File getDirectoryFromPath(String path) {
        return null;
    }

    @Override
    public File getFileFromPath(String path) {
        return null;
    }

    @Override
    public void writeErrorLog(Throwable exception) {

    }

    @Override
    public void writeErrorLog(Throwable exception, String comment) {

    }

    @Override
    public File getErrorLogFile() {
        return null;
    }

    @Override
    public String getErrorLog() throws FileNotFoundException {
        return null;
    }

    @Override
    public String getSystemInformation() {
        return null;
    }

    @Override
    public File unzipResourceToTempLocation(String resourceLocation) throws IOException {
        return null;
    }

    @Override
    public void unzipResourceToDir(String internalPath, Path target) throws IOException {

    }

    @Override
    public void addFileAttributeOSX(String filePath, String attribute, String value, boolean isHexVal) throws Exception {

    }

    @Override
    public String getFileAttributeOSX(String filePath, String attribute) throws Exception {
        return null;
    }

    @Override
    public String[] runAtConsole(String[] arguments, boolean addSpaces) {
        return new String[0];
    }

    @Override
    public String getTerminalJavaVersion() {
        return null;
    }

    @Override
    public boolean isJavaAvailableInTerminal() {
        return false;
    }

    @Override
    public byte[] clearCarrigeReturns(byte[] filthyWithWindows) {
        return new byte[0];
    }

    @Override
    public byte[] loadImageBytes(String path) throws IOException {
        return new byte[0];
    }
}

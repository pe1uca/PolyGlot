package org.darisadesigns.polyglotlina.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.darisadesigns.polyglotlina.CustHandler;
import org.darisadesigns.polyglotlina.CustHandlerFactory;
import org.darisadesigns.polyglotlina.CustomControls.GrammarChapNode;
import org.darisadesigns.polyglotlina.CustomControls.GrammarSectionNode;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.IOHandler;
import org.darisadesigns.polyglotlina.ManagersCollections.GrammarManager;
import org.darisadesigns.polyglotlina.ManagersCollections.ImageCollection;
import org.darisadesigns.polyglotlina.ManagersCollections.LogoCollection;
import org.darisadesigns.polyglotlina.ManagersCollections.ReversionManager;
import org.darisadesigns.polyglotlina.Nodes.ImageNode;
import org.darisadesigns.polyglotlina.Nodes.LogoNode;
import org.darisadesigns.polyglotlina.Nodes.ReversionNode;
import org.darisadesigns.polyglotlina.PFontHandler;
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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class AndroidIOHandler implements IOHandler {
    private static final String TAG = "IOHandler";

    private final Context context;
    private final Activity activity;

    public AndroidIOHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
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
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        }
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
    public void writeFile(String _fileName, Document doc, DictCore core, File workingDirectory, Instant saveTime, boolean writeToReversionMgr) throws IOException, TransformerException {
        File finalFile = new File(_fileName);
        String writeLog = "";
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        try (StringWriter writer = new StringWriter()) {
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            StringBuilder sb = new StringBuilder();
            sb.append(writer.getBuffer());
            byte[] xmlData = sb.toString().getBytes(StandardCharsets.UTF_8);

            final File tmpSaveLocation = makeTempSaveFile(workingDirectory);

            try (FileOutputStream fileOutputStream = new FileOutputStream(tmpSaveLocation)) {
                try (ZipOutputStream out = new ZipOutputStream(fileOutputStream, StandardCharsets.UTF_8)) {
                    ZipEntry e = new ZipEntry(PGTUtil.LANG_FILE_NAME);
                    out.putNextEntry(e);

                    out.write(xmlData, 0, xmlData.length);

                    out.closeEntry();

                    /*writeLog += PFontHandler.writeFont(out,
                            ((DesktopPropertiesManager)core.getPropertiesManager()).getFontCon(),
                            core.getPropertiesManager().getCachedFont(),
                            core,
                            true);

                    writeLog += PFontHandler.writeFont(out,
                            ((DesktopPropertiesManager)core.getPropertiesManager()).getFontLocal(),
                            core.getPropertiesManager().getCachedLocalFont(),
                            core,
                            false);*/

                    writeLog += writeLogoNodesToArchive(out, core);
                    writeLog += writeImagesToArchive(out, core);
                    writeLog += writeWavToArchive(out, core);
                    writeLog += writePriorStatesToArchive(out, core);

                    out.finish();
                }
            }

            // attempt to open file in dummy core. On success, copy file to end
            // destination, on fail, delete file and inform user by bubbling error
            try {
                // pass null shell class because this will ultimately be discarded
                AndroidOSHandler osHandler = new AndroidOSHandler(
                        new AndroidIOHandler(context, activity),
                        new AndroidInfoBox(activity),
                        new AndroidHelpHandler(),
                        new AndroidPFontHandler(),
                        context
                );
                DictCore test = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil(), new AndroidGrammarManager());
                test.readFile(tmpSaveLocation.getAbsolutePath());
            }
            catch (Exception ex) {
                throw new IOException(ex);
            }

            try {
                copyFile(tmpSaveLocation.toPath(), finalFile.toPath(), true);
                tmpSaveLocation.delete(); // wipe temp file if successful
            }
            catch (IOException e) {
                throw new IOException("Unable to save file: " + e.getMessage(), e);
            }

            core.getReversionManager().addVersion(xmlData, saveTime);
        }

        if (!writeLog.isEmpty()) {
            core.getOSHandler().getInfoBox().warning("File Save Issues", "Problems encountered when saving file " + _fileName + writeLog);
        }
    }

    private File makeTempSaveFile(File workingDirectory) throws IOException {
        return File.createTempFile(PGTUtil.TEMP_FILE, ".pgd");
    }

    private String writeLogoNodesToArchive(ZipOutputStream out, DictCore core) {
        String writeLog = "";
        LogoNode[] logoNodes = core.getLogoCollection().getAllLogos();
        if (logoNodes.length != 0) {
            try {
                out.putNextEntry(new ZipEntry(PGTUtil.LOGOGRAPH_SAVE_PATH));
                for (LogoNode curNode : logoNodes) {
                    try {
                        out.putNextEntry(new ZipEntry(PGTUtil.LOGOGRAPH_SAVE_PATH
                                + curNode.getId() + ".png"));

                        Bitmap bitmap = BitmapFactory.decodeByteArray(curNode.getLogoBytes(), 0, curNode.getLogoBytes().length);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.closeEntry();
                    }
                    catch (IOException e) {
                        writeErrorLog(e);
                        writeLog += "\nUnable to save logograph: " + e.getLocalizedMessage();
                    }
                }
            }
            catch (IOException e) {
                writeErrorLog(e);
                writeLog += "\nUnable to save Logographs: " + e.getLocalizedMessage();
            }
        }

        return writeLog;
    }

    private String writeImagesToArchive(ZipOutputStream out, DictCore core) {
        String writeLog = "";
        ImageNode[] imageNodes = core.getImageCollection().getAllImages();
        if (imageNodes.length != 0) {
            try {
                out.putNextEntry(new ZipEntry(PGTUtil.IMAGES_SAVE_PATH));
                for (ImageNode curNode : imageNodes) {
                    try {
                        out.putNextEntry(new ZipEntry(PGTUtil.IMAGES_SAVE_PATH
                                + curNode.getId() + ".png"));

                        Bitmap bitmap = BitmapFactory.decodeByteArray(curNode.getImageBytes(), 0, curNode.getImageBytes().length);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.closeEntry();
                    }
                    catch (IOException e) {
                        writeErrorLog(e);
                        writeLog += "\nUnable to save image: " + e.getLocalizedMessage();
                    }
                }
            }
            catch (IOException e) {
                writeErrorLog(e);
                writeLog += "\nUnable to save Images: " + e.getLocalizedMessage();
            }
        }
        return writeLog;
    }

    private String writeWavToArchive(ZipOutputStream out, DictCore core) {
        String writeLog = "";
        Map<Integer, byte[]> grammarSoundMap = core.getGrammarManager().getSoundMap();
        Iterator<Entry<Integer, byte[]>> gramSoundIt = grammarSoundMap.entrySet().iterator();
        if (gramSoundIt.hasNext()) {
            try {
                out.putNextEntry(new ZipEntry(PGTUtil.GRAMMAR_SOUNDS_SAVE_PATH));

                while (gramSoundIt.hasNext()) {
                    Entry<Integer, byte[]> curEntry = gramSoundIt.next();
                    Integer curId = curEntry.getKey();
                    byte[] curSound = curEntry.getValue();

                    try {
                        out.putNextEntry(new ZipEntry(PGTUtil.GRAMMAR_SOUNDS_SAVE_PATH
                                + curId + ".raw"));
                        out.write(curSound);
                        out.closeEntry();
                    }
                    catch (IOException e) {
                        writeErrorLog(e);
                        writeLog += "\nUnable to save sound: " + e.getLocalizedMessage();
                    }

                }
            }
            catch (IOException e) {
                writeErrorLog(e);
                writeLog += "\nUnable to save sounds: " + e.getLocalizedMessage();
            }
        }
        return writeLog;
    }

    private String writePriorStatesToArchive(ZipOutputStream out, DictCore core) throws IOException {
        String writeLog = "";
        ReversionNode[] reversionList = core.getReversionManager().getReversionList();

        try {
            out.putNextEntry(new ZipEntry(PGTUtil.REVERSION_SAVE_PATH));

            for (int i = 0; i < reversionList.length; i++) {
                ReversionNode node = reversionList[i];

                out.putNextEntry(new ZipEntry(PGTUtil.REVERSION_SAVE_PATH + PGTUtil.REVERSION_BASE_FILE_NAME + i));
                out.write(node.getValue());
                out.closeEntry();
            }
        }
        catch (IOException e) {
            throw new IOException("Unable to create reversion files.", e);
        }

        return writeLog;
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
        StandardCopyOption option = replaceExisting ? StandardCopyOption.REPLACE_EXISTING : StandardCopyOption.ATOMIC_MOVE;
        Files.copy(fromLocation, toLocation, option);
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
        try (ZipFile zipFile = new ZipFile(fileName)) {
            for (LogoNode curNode : logoCollection.getAllLogos()) {
                if (curNode.getLogoBytes() != null &&
                        curNode.getLogoBytes().length != 0)
                    continue;
                ZipEntry imgEntry = zipFile.getEntry(PGTUtil.LOGOGRAPH_SAVE_PATH
                        + curNode.getId() + ".png");

                byte[] img;
                try (InputStream imageStream = zipFile.getInputStream(imgEntry)) {
                    img = this.loadImageBytesFromStream(imageStream);
                }
                curNode.setLogoBytes(img);
            }
        }
    }

    public byte[] loadImageBytesFromStream(InputStream stream) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        return loadImageBytesFromBitmap(bitmap);
    }

    @Override
    public void loadReversionStates(ReversionManager reversionManager, String fileName) throws IOException {
        try (ZipFile zipFile = new ZipFile(fileName)) {
            int i = 0;

            ZipEntry reversion = zipFile.getEntry(PGTUtil.REVERSION_SAVE_PATH
                    + PGTUtil.REVERSION_BASE_FILE_NAME + i);

            while (reversion != null && i < reversionManager.getMaxReversionsCount()) {
                reversionManager.addVersionToEnd(streamToByetArray(zipFile.getInputStream(reversion)));
                i++;
                reversion = zipFile.getEntry(PGTUtil.REVERSION_SAVE_PATH
                        + PGTUtil.REVERSION_BASE_FILE_NAME + i);
            }

            // remember to load latest state in addition to all prior ones
            reversion = zipFile.getEntry(PGTUtil.LANG_FILE_NAME);
            reversionManager.addVersionToEnd(streamToByetArray(zipFile.getInputStream(reversion)));
        }
    }

    @Override
    public void exportConFont(String exportPath, String dictionaryPath) throws IOException {
        missingImplementation();
    }

    @Override
    public void exportLocalFont(String exportPath, String dictionaryPath) throws IOException {
        missingImplementation();
    }

    @Override
    public void exportCharisFont(String exportPath) throws IOException {
        missingImplementation();
    }

    @Override
    public void loadGrammarSounds(String fileName, GrammarManager grammarManager) throws Exception {
        String loadLog = "";

        try (ZipFile zipFile = new ZipFile(fileName)) {
            for (GrammarChapNode curChap : grammarManager.getChapters()) {
                AndroidGrammarChapNode aCurChap = (AndroidGrammarChapNode)curChap;
                for (int i = 0; i < aCurChap.getChildCount(); i++) {

                    GrammarSectionNode curNode = (GrammarSectionNode) aCurChap.children.get(i);

                    if (curNode.getRecordingId() == -1) {
                        continue;
                    }

                    String soundPath = PGTUtil.GRAMMAR_SOUNDS_SAVE_PATH
                            + curNode.getRecordingId() + ".raw";
                    ZipEntry soundEntry = zipFile.getEntry(soundPath);

                    byte[] sound = null;

                    try (InputStream soundStream = zipFile.getInputStream(soundEntry)) {
                        sound = streamToByetArray(soundStream);
                    } catch (Exception e) {
                        writeErrorLog(e);
                        loadLog += "\nUnable to load sound: " + e.getLocalizedMessage();
                    }

                    if (sound == null) {
                        continue;
                    }

                    grammarManager.addChangeRecording(curNode.getRecordingId(), sound);
                }
            }
        }

        if (!loadLog.isEmpty()) {
            throw new Exception(loadLog);
        }
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
    public boolean isJavaAvailable() {
        missingImplementation();
        return true;
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

    public void moveInputToOutput(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        if (null != from) {
            while((len = from.read(buf)) > 0) {
                to.write(buf, 0, len);
            }
            to.close();
            from.close();
        }
    }
}

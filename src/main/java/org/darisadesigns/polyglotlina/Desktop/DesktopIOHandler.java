/*
 * Copyright (c) 2014-2022, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.darisadesigns.polyglotlina.Desktop;

import org.darisadesigns.polyglotlina.Nodes.LogoNode;
import org.darisadesigns.polyglotlina.ManagersCollections.GrammarManager;
import org.darisadesigns.polyglotlina.ManagersCollections.LogoCollection;
import org.darisadesigns.polyglotlina.CustomControls.GrammarSectionNode;
import org.darisadesigns.polyglotlina.CustomControls.GrammarChapNode;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.ManagersCollections.ImageCollection;
import org.darisadesigns.polyglotlina.Desktop.ManagersCollections.DesktopOptionsManager;
import org.darisadesigns.polyglotlina.ManagersCollections.ReversionManager;
import org.darisadesigns.polyglotlina.Nodes.ImageNode;
import org.darisadesigns.polyglotlina.Nodes.ReversionNode;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.darisadesigns.polyglotlina.CustHandler;
import org.darisadesigns.polyglotlina.CustHandlerFactory;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopGrammarChapNode;
import org.darisadesigns.polyglotlina.Desktop.ManagersCollections.DesktopGrammarManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.IOHandler;
import org.darisadesigns.polyglotlina.XMLRecoveryTool;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class handles file IO for PolyGlot
 *
 * @author draque
 */
public final class DesktopIOHandler implements IOHandler {

    private static DesktopIOHandler ioHandler;

    /**
     * Opens and returns image from URL given (can be file path)
     *
     * @param filePath path of image
     * @return BufferedImage
     * @throws IOException in IO
     */
    public BufferedImage getImage(String filePath) throws IOException {
        return ImageIO.read(new File(filePath));
    }

    /**
     * Creates and returns a temporary file with the contents specified. File
     * will be deleted on exit of PolyGlot.
     *
     * @param contents Contents to put in file.
     * @param extension extension name for tmp file (defaults to tmp if none
     * given)
     * @return Temporary file with specified contents
     * @throws IOException on write error
     */
    @Override
    public File createTmpFileWithContents(String contents, String extension) throws IOException {
        File ret = File.createTempFile("POLYGLOT", extension);

        try ( Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ret), "UTF8"))) {
            out.append(contents);
            out.flush();
        }

        ret.deleteOnExit();

        return ret;
    }

    @Override
    public File createTmpFileFromImageBytes(byte[] imageBytes, String fileName) throws IOException {
        File tmpFile = File.createTempFile(fileName, ".png");
        ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
        BufferedImage img = ImageIO.read(stream);
        ImageIO.write(
                img,
                "PNG",
                new FileOutputStream(tmpFile)
        );
        return tmpFile;
    }

    @Override
    public File createFileWithContents(String path, String contents) throws IOException {
        File ret = new File(path);

        if (!ret.exists() && !ret.createNewFile()) {
            throw new IOException("Unable to create: " + path);
        }

        try ( Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ret), "UTF8"))) {
            out.write(contents);
            out.flush();
        }

        return ret;
    }

    @Override
    public byte[] getByteArrayFromFile(File file) throws IOException {
        try ( InputStream inputStream = new FileInputStream(file)) {
            return streamToByetArray(inputStream);
        }
    }

    /**
     * Takes input stream and converts it to a raw byte array
     *
     * @param is
     * @return raw byte representation of stream
     * @throws IOException
     */
    @Override
    public byte[] streamToByetArray(InputStream is) throws IOException {
        try ( ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        }
    }

    /**
     * Used for snagging catchable versions of files
     *
     * @param filePath path of file to fetch as byte array
     * @return byte array of file at given path
     * @throws java.io.FileNotFoundException
     */
    @Override
    public byte[] getFileByteArray(String filePath) throws IOException {
        byte[] ret;
        final File toByteArrayFile = new File(filePath);

        try ( InputStream inputStream = new FileInputStream(toByteArrayFile)) {
            ret = streamToByetArray(inputStream);
        }

        return ret;
    }

    @Override
    /**
     * Given file name, returns appropriate cust handler
     *
     * @param _fileName full path of target file to read
     * @param _core dictionary core
     * @return CustHandler class
     * @throws java.io.IOException on read problem
     */
    public CustHandler getHandlerFromFile(String _fileName, DictCore _core) throws IOException {
        CustHandler ret = null;

        if (isFileZipArchive(_fileName)) {
            try ( ZipFile zipFile = new ZipFile(_fileName)) {
                ZipEntry xmlEntry = zipFile.getEntry(PGTUtil.LANG_FILE_NAME);
                
                if (xmlEntry == null) {
                    throw new IOException("PGD file corrupt. Unable to read required file from archive: " + PGTUtil.LANG_FILE_NAME);
                }
                
                try ( InputStream ioStream = zipFile.getInputStream(xmlEntry)) {
                    ret = CustHandlerFactory.getCustHandler(ioStream, _core);
                } catch (SAXParseException e) {
                    new DesktopInfoBox(null).warning("Data corruption detected", "Data corruption has been detected in your save. Attempting to recover.");
                    
                    try ( InputStream ioStream = zipFile.getInputStream(xmlEntry)) {
                        var xml = "";
                        for (var myByte : ioStream.readAllBytes()) {
                            xml += (char)myByte;
                        }

                        var xmlRecoveryTool = new XMLRecoveryTool(xml);
                        xml = xmlRecoveryTool.recoverXml();

                        ret = CustHandlerFactory.getCustHandler(new ByteArrayInputStream(xml.getBytes()), _core);
                        new DesktopInfoBox(null).info("Success", "Recovery successful!");
                    } catch (Exception ex) {
                        throw new IOException(e.getLocalizedMessage(), ex);
                    }
                } catch (Exception e) {
                    throw new IOException(e.getLocalizedMessage(), e);
                }
            }
        } else {
            try ( InputStream ioStream = new FileInputStream(_fileName)) {
                ret = CustHandlerFactory.getCustHandler(ioStream, _core);
            } catch (Exception e) {
                throw new IOException(e.getLocalizedMessage(), e);
            }
        }

        return ret;
    }

    @Override
    /**
     * Creates a custhandler object from a reversion byte array of a language
     * state
     *
     * @param byteArray byte array containing XML of language state
     * @param _core dictionary core
     * @return new custhandler class
     * @throws IOException on parse error
     */
    public CustHandler getHandlerFromByteArray(byte[] byteArray, DictCore _core) throws IOException {
        try {
            return CustHandlerFactory.getCustHandler(new ByteArrayInputStream(byteArray), _core);
        }
        catch (Exception e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Opens an image via GUI and returns as buffered image Returns null if user
     * cancels.
     *
     * @param parent parent window of operation
     * @param workingDirectory
     * @return buffered image selected by user
     * @throws IOException on file read error
     */
    public BufferedImage openImage(Window parent, File workingDirectory) throws IOException {
        BufferedImage ret = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Image");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "tiff", "bmp", "png");
        chooser.setFileFilter(filter);
        String fileName;
        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
            ret = ImageIO.read(new File(fileName));
        }

        return ret;
    }

    /**
     * returns name of file sans path
     *
     * @param fullPath full path to file
     * @return string of filename
     */
    @Override
    public String getFilenameFromPath(String fullPath) {
        File file = new File(fullPath);
        return file.getName();
    }

    /**
     * Deletes options file
     *
     * @param workingDirectory
     */
    @Override
    public void deleteIni(String workingDirectory) {
        File f = new File(workingDirectory + File.separator + PGTUtil.POLYGLOT_INI);
        if (!f.exists()) {
            return;
        }

        try {
            f.delete();
        }
        catch (Exception e) {
            // can't write to folder, so don't bother trying to write log file...
            // IOHandler.writeErrorLog(e);
            new DesktopInfoBox().error("Permissions Error", "PolyGlot lacks permissions to write to its native folder.\n"
                    + "Please move to a folder with full write permissions: " + e.getLocalizedMessage());
        }
    }

    @Override
    /**
     * Given handler class, parses XML document within file (archive or not)
     *
     * @param _fileName full path of target file
     * @param _handler custom handler to consume XML document
     * @throws IOException on read error
     * @throws ParserConfigurationException on parser factory config error
     * @throws SAXException on XML interpretation error
     */
    public void parseHandler(String _fileName, CustHandler _handler)
            throws IOException, ParserConfigurationException, SAXException {
        try ( ZipFile zipFile = new ZipFile(_fileName)) {
            ZipEntry xmlEntry = zipFile.getEntry(PGTUtil.LANG_FILE_NAME);
            
            if (xmlEntry == null) {
                throw new IOException("PGD file corrupt. Unable to read required file from archive: " + PGTUtil.LANG_FILE_NAME);
            }
            
            try ( InputStream ioStream = zipFile.getInputStream(xmlEntry)) {
                parseHandlerInternal(ioStream, _handler);
            } catch (SAXParseException e) {
                try ( InputStream ioStream = zipFile.getInputStream(xmlEntry)) {
                    var xml = "";
                    for (var myByte : ioStream.readAllBytes()) {
                        xml += (char)myByte;
                    }

                    var xmlRecoveryTool = new XMLRecoveryTool(xml);
                    xml = xmlRecoveryTool.recoverXml();

                    parseHandlerInternal(new ByteArrayInputStream(xml.getBytes()), _handler);
                } catch (Exception ex) {
                    throw new IOException(e.getLocalizedMessage(), ex);
                }
            }
        }
    }

    @Override
    public void parseHandlerByteArray(byte[] reversion, CustHandler _handler)
            throws ParserConfigurationException, IOException, SAXException {
        parseHandlerInternal(new ByteArrayInputStream(reversion), _handler);
    }

    private void parseHandlerInternal(InputStream stream, CustHandler _handler)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(stream, _handler);
    }

    /**
     * Tests whether or not a file is a zip archive
     *
     * @param _fileName the file to test
     * @return true is passed file is a zip archive
     * @throws java.io.FileNotFoundException
     */
    @Override
    public boolean isFileZipArchive(String _fileName) throws IOException {
        File file = new File(_fileName);

        // ignore directories and files too small to possibly be archives
        if (file.isDirectory()
                || file.length() < 4) {
            return false;
        }

        int test;
        try ( FileInputStream fileStream = new FileInputStream(file)) {
            try ( BufferedInputStream buffer = new BufferedInputStream(fileStream)) {
                try ( DataInputStream in = new DataInputStream(buffer)) {
                    test = in.readInt();
                }
            }
        }
        return test == 0x504b0304;
    }

    @Override
    public void writeFile(
            String _fileName,
            Document doc,
            DictCore core,
            File workingDirectory,
            Instant saveTime,
            boolean writeToReversionMgr
    )
            throws IOException, TransformerException {
        File finalFile = new File(_fileName);
        String writeLog;
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        try (StringWriter writer = new StringWriter()) {
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            StringBuilder sb = new StringBuilder();
            sb.append(writer.getBuffer());
            byte[] xmlData = sb.toString().getBytes(StandardCharsets.UTF_8);

            final File tmpSaveLocation = makeTempSaveFile(workingDirectory);
            
            writeLog = writeRawFileOutput(tmpSaveLocation, xmlData, core);

            // copy tmp file to final location folder
            var tmpSaveFinalLocation = new File(finalFile.getParent() + File.separator + tmpSaveLocation.getName());
            copyFile(tmpSaveLocation.toPath(), tmpSaveFinalLocation.toPath(), true);

            // attempt to open file in dummy core. On success, copy file to end
            // destination, on fail, delete file and inform user by bubbling error
            try {
                // pass null shell class because this will ultimately be discarded
                DesktopHelpHandler helpHandler = new DesktopHelpHandler();
                PFontHandler fontHandler = new PFontHandler();
                var osHandler = new DesktopOSHandler(DesktopIOHandler.getInstance(), new DummyInfoBox(), helpHandler, fontHandler);
                DictCore test = new DictCore(new DesktopPropertiesManager(), osHandler, new PGTUtil(), new DesktopGrammarManager());
                PolyGlot.getTestShell(test);
                test.readFile(tmpSaveFinalLocation.getAbsolutePath());
                
                if (!core.equals(test)) {
                    throw new Exception("Written file does not match file in memory.");
                }
            } catch (Exception ex) {
                throw new IOException(ex);
            }
            
            if (finalFile.exists()) {
                finalFile.delete();
            }
            
            tmpSaveFinalLocation.renameTo(finalFile);
            tmpSaveLocation.delete(); // wipe temp file if successful

            if (writeToReversionMgr) {
                core.getReversionManager().addVersion(xmlData, saveTime);
            }
        }

        if (!writeLog.isEmpty()) {
            new DesktopInfoBox().warning("File Save Issues", "Problems encountered when saving file " + _fileName + writeLog);
        }
    }
    
    /**
     * Creates raw output file (processed for safety/security upstream)
     */
    private String writeRawFileOutput(File tmpSaveLocation, byte[] xmlData, DictCore core) throws FileNotFoundException, IOException {
        String writeLog;
        
        try (FileOutputStream fileOutputStream = new FileOutputStream(tmpSaveLocation)) {
            try ( ZipOutputStream out = new ZipOutputStream(fileOutputStream, StandardCharsets.UTF_8)) {
                ZipEntry e = new ZipEntry(PGTUtil.LANG_FILE_NAME);
                out.putNextEntry(e);

                out.write(xmlData, 0, xmlData.length);

                out.closeEntry();

                writeLog = PFontHandler.writeFont(out,
                        ((DesktopPropertiesManager) core.getPropertiesManager()).getFontCon(),
                        core.getPropertiesManager().getCachedFont(),
                        core,
                        true);

                writeLog += PFontHandler.writeFont(out,
                        ((DesktopPropertiesManager) core.getPropertiesManager()).getFontLocal(),
                        core.getPropertiesManager().getCachedLocalFont(),
                        core,
                        false);

                writeLog += writeLogoNodesToArchive(out, core);
                writeLog += writeImagesToArchive(out, core);
                writeLog += writeWavToArchive(out, core);
                writeLog += writePriorStatesToArchive(out, core);

                out.finish();
            }
        }
        
        return writeLog;
    }

    /**
     * Gets temporary file when saving PolyGlot archive. If temporary file
     * already exists, backs file up based on current epoch second then creates
     * a new temp file
     *
     * @param workingDirectory
     * @return
     */
    private File makeTempSaveFile(File workingDirectory) {
        File ret = new File(workingDirectory + File.separator + PGTUtil.TEMP_FILE);

        if (ret.exists()) {
            File backupTemp = new File(ret.getAbsolutePath() + Instant.now().getEpochSecond());
            ret.renameTo(backupTemp);
            ret = new File(workingDirectory + File.separator + PGTUtil.TEMP_FILE);
        }

        return ret;
    }

    /**
     * Gets most recent temporary save file if one exists, null otherwise
     *
     * @param workingDirectory
     * @return
     */
    @Override
    public File getTempSaveFileIfExists(File workingDirectory) {
        File ret = new File(workingDirectory + File.separator + PGTUtil.TEMP_FILE);

        // search for backed up tmp files (possible due to stranding) if basic does not exist
        if (!ret.exists()) {
            ret = null;

            for (File curFile : workingDirectory.listFiles()) {
                if (curFile.getName().startsWith(PGTUtil.TEMP_FILE)) {
                    ret = curFile;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Moves file to archive folder with name prefixed with current epoch time
     *
     * @param source
     * @param workingDirectory
     * @return
     * @throws IOException
     */
    @Override
    public File archiveFile(File source, File workingDirectory) throws IOException {
        String workingDirectoryPath = workingDirectory.getCanonicalPath();
        File dest = new File(workingDirectoryPath
                + File.separator
                + Instant.now().getEpochSecond()
                + "_" + source.getName()
                + ".archive");

        source.renameTo(dest);

        return dest;
    }

    @Override
    public void copyFile(Path fromLocation, Path toLocation, boolean replaceExisting) throws IOException {
        StandardCopyOption option = replaceExisting ? StandardCopyOption.REPLACE_EXISTING : StandardCopyOption.ATOMIC_MOVE;
        Files.copy(fromLocation, toLocation, option);
        
        if (!new File(toLocation.toString()).exists()) {
            throw new IOException("File " + toLocation.toString() + " could not be written to its target destination.\n"
                + "Please try another location or change destination permissions.");
        }
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

                        BufferedImage write = ImageIO.read(new ByteArrayInputStream(curNode.getLogoBytes()));
                        ImageIO.write(write, "png", out);

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

    private String writeWavToArchive(ZipOutputStream out, DictCore core) {
        String writeLog = "";
        Map<Integer, byte[]> grammarSoundMap = ((DesktopGrammarManager) core.getGrammarManager()).getSoundMap();
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

                        BufferedImage write = ImageIO.read(new ByteArrayInputStream(curNode.getImageBytes()));
                        ImageIO.write(write, "png", out);

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

    /**
     * Tests whether a file at a particular location exists. Wrapped to avoid IO
     * code outside this file
     *
     * @param fullPath path of file to test
     * @return true if file exists, false otherwise
     */
    @Override
    public boolean fileExists(String fullPath) {
        File f = new File(fullPath);
        return f.exists();
    }

    /**
     * Loads image assets from file. Does not load logographs due to legacy
     * coding/logic
     *
     * @param imageCollection from dictCore to populate
     * @param fileName of file containing assets
     * @throws java.io.IOException
     */
    @Override
    public void loadImageAssets(ImageCollection imageCollection,
            String fileName) throws Exception {
        try ( ZipFile zipFile = new ZipFile(fileName)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) { // find images directory (zip paths are linear, only simulating tree structure)
                ZipEntry entry = entries.nextElement();
                if (!entry.getName().equals(PGTUtil.IMAGES_SAVE_PATH)) {
                    continue;
                }
                break;
            }

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                
                // Linear nature of zip files makes this necessary
                if (!entry.getName().startsWith(PGTUtil.IMAGES_SAVE_PATH)) {
                    continue;
                }

                if (entry.isDirectory()) { // kills process after last image found
                    break;
                }

                BufferedImage img;
                try ( InputStream imageStream = zipFile.getInputStream(entry)) {
                    String name = entry.getName();
                    name = name.replace(".png", "")
                            .replace(PGTUtil.IMAGES_SAVE_PATH, "");
                    int imageId = Integer.parseInt(name);
                    img = ImageIO.read(imageStream);
                    ImageNode imageNode = new ImageNode(PolyGlot.getPolyGlot().getCore());
                    imageNode.setId(imageId);
                    imageNode.setImageBytes(loadImageBytesFromImage(img));
                    imageCollection.getBuffer().setEqual(imageNode);
                    imageCollection.insert(imageId);
                }
            }
        }
    }

    /**
     * loads all images into their logographs from archive and images into the
     * generalized image collection
     *
     * @param logoCollection logocollection from dictionary core
     * @param fileName name/path of archive
     * @throws java.lang.Exception
     */
    @Override
    public void loadLogographs(LogoCollection logoCollection,
            String fileName) throws Exception {
        try ( ZipFile zipFile = new ZipFile(fileName)) {
            for (LogoNode curNode : logoCollection.getAllLogos()) {
                ZipEntry imgEntry = zipFile.getEntry(PGTUtil.LOGOGRAPH_SAVE_PATH
                        + curNode.getId() + ".png");
                
                if (imgEntry == null) {
                    continue;
                }

                try (InputStream imageStream = zipFile.getInputStream(imgEntry)) {
                    var img = ImageIO.read(imageStream);
                    curNode.setLogoBytes(loadImageBytesFromImage(img));
                }
            }
        }
    }

    /**
     * Loads all reversion XML files from polyglot archive
     *
     * @param reversionManager reversion manager to load to
     * @param fileName full path of polyglot archive
     * @throws IOException on read error
     */
    @Override
    public void loadReversionStates(ReversionManager reversionManager,
            String fileName) throws IOException {
        try ( ZipFile zipFile = new ZipFile(fileName)) {
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

    /**
     * Exports font in PGD to external file
     *
     * @param exportPath path to export to
     * @param dictionaryPath path of PGT dictionary
     * @throws IOException
     */
    @Override
    public void exportConFont(String exportPath, String dictionaryPath) throws IOException {
        exportFont(exportPath, dictionaryPath, PGTUtil.CON_FONT_FILE_NAME);
    }

    @Override
    public void exportLocalFont(String exportPath, String dictionaryPath) throws IOException {
        exportFont(exportPath, dictionaryPath, PGTUtil.LOCAL_FONT_FILE_NAME);
    }

    public void exportFont(String exportPath, String dictionaryPath, String exportFontFilename) throws IOException {
        try ( ZipFile zipFile = new ZipFile(dictionaryPath)) {
            // ensure export file has the proper extension
            if (!exportPath.toLowerCase().endsWith(".ttf")) {
                exportPath += ".ttf";
            }

            ZipEntry fontEntry = zipFile.getEntry(exportFontFilename);

            if (fontEntry != null) {
                Path path = Paths.get(exportPath);
                try ( InputStream copyStream = zipFile.getInputStream(fontEntry)) {
                    Files.copy(copyStream, path, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                throw new IOException(exportFontFilename + " not found in PGD language file.");
            }
        }
    }

    /**
     * Exports Charis unicode font to specified location
     *
     * @param exportPath full export path
     * @throws IOException on failure
     */
    @Override
    public void exportCharisFont(String exportPath) throws IOException {
        try ( InputStream fontStream = IOHandler.class.getResourceAsStream(PGTUtil.UNICODE_FONT_LOCATION)) {
            byte[] buffer = new byte[fontStream.available()];
            fontStream.read(buffer);

            try ( OutputStream oStream = new FileOutputStream(new File(exportPath))) {
                oStream.write(buffer);
            }
        }
    }

    /**
     * Loads any related grammar recordings into the passed grammar manager via
     * id
     *
     * @param fileName name of file to load sound recordings from
     * @param grammarManager grammar manager to populate with sounds
     * @throws Exception on sound load errors
     */
    @Override
    public void loadGrammarSounds(String fileName, GrammarManager grammarManager) throws Exception {
        String loadLog = "";

        try ( ZipFile zipFile = new ZipFile(fileName)) {
            for (GrammarChapNode curChap : grammarManager.getChapters()) {
                for (int i = 0; i < curChap.getChildCount(); i++) {
                    GrammarSectionNode curNode = (GrammarSectionNode) ((DesktopGrammarChapNode) curChap).getChildAt(i);

                    if (curNode.getRecordingId() == -1) {
                        continue;
                    }

                    String soundPath = PGTUtil.GRAMMAR_SOUNDS_SAVE_PATH
                            + curNode.getRecordingId() + ".raw";
                    ZipEntry soundEntry = zipFile.getEntry(soundPath);

                    byte[] sound = null;

                    try ( InputStream soundStream = zipFile.getInputStream(soundEntry)) {
                        sound = streamToByetArray(soundStream);
                    }
                    catch (IOException e) {
                        writeErrorLog(e);
                        loadLog += "\nUnable to load sound: " + e.getLocalizedMessage();
                    }
                    catch (Exception e) {
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

    /**
     * Tests whether the path can be written to
     *
     * @param path
     * @return
     */
    private boolean testCanWrite(String path) {
        return new File(path).canWrite();
    }

    /**
     * Saves ini file with polyglot options
     *
     * @param workingDirectory
     * @param opMan
     * @throws IOException on failure or lack of permission to write
     */
    public void writeOptionsIni(String workingDirectory, DesktopOptionsManager opMan) throws IOException {
        try ( Writer f0 = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(workingDirectory
                        + File.separator + PGTUtil.POLYGLOT_INI), StandardCharsets.UTF_8))) {
            String newLine = System.getProperty("line.separator");
            String nextLine;

            if (!testCanWrite(workingDirectory + File.separator + PGTUtil.POLYGLOT_INI)) {
                throw new IOException("Unable to save settings. Polyglot does not have permission to write to folder: "
                        + workingDirectory
                        + ". This is most common when running from Program Files in Windows.");
            }

            nextLine = PGTUtil.OPTIONS_LAST_FILES + "=";
            int lastFileCount = 0;
            for (String file : opMan.getLastFiles()) {
                if (lastFileCount > PGTUtil.OPTIONS_NUM_LAST_FILES) {
                    break;
                }

                lastFileCount++;
                // only write to ini if 1) the max file path length is not absurd/garbage, and 2) the file exists
                if (file.length() < PGTUtil.MAX_FILE_PATH_LENGTH && new File(file).exists()) {
                    if (nextLine.endsWith("=")) {
                        nextLine += file;
                    } else {
                        nextLine += ("," + file);
                    }
                }
            }
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_SCREEN_POS + "=";
            for (Entry<String, Point> curPos : opMan.getScreenPositions().entrySet()) {
                nextLine += ("," + curPos.getKey() + ":" + curPos.getValue().x + ":"
                        + curPos.getValue().y);
            }
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_SCREENS_SIZE + "=";
            for (Entry<String, Dimension> curSize : opMan.getScreenSizes().entrySet()) {
                nextLine += ("," + curSize.getKey() + ":" + curSize.getValue().width + ":"
                        + curSize.getValue().height);
            }

            f0.write(nextLine + newLine);
            nextLine = PGTUtil.OPTIONS_SCREENS_OPEN + "=";

            for (String screen : opMan.getLastScreensUp()) {
                nextLine += ("," + screen);
            }
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_AUTO_RESIZE + "="
                    + (opMan.isAnimateWindows() ? PGTUtil.TRUE : PGTUtil.FALSE);
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_NIGHT_MODE + "="
                    + (opMan.isNightMode() ? PGTUtil.TRUE : PGTUtil.FALSE);
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_REVERSIONS_COUNT + "=" + opMan.getMaxReversionCount();
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_TODO_DIV_LOCATION + "=" + opMan.getToDoBarPosition();
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_MAXIMIZED + "=" + (opMan.isMaximized() ? PGTUtil.TRUE : PGTUtil.FALSE);
            f0.write(nextLine + newLine);

            nextLine = PGTUtil.OPTIONS_DIVIDER_POSITION + "=";
            for (Entry<String, Integer> location : opMan.getDividerPositions().entrySet()) {
                nextLine += ("," + location.getKey() + ":" + location.getValue());
            }
            f0.write(nextLine + newLine);
            
            nextLine = PGTUtil.OPTIONS_MSBETWEENSAVES + "=" + opMan.getMsBetweenSaves();
            f0.write(nextLine + newLine);
            
            nextLine = PGTUtil.OPTIONS_UI_SCALE + "=" + opMan.getUiScale();
            f0.write(nextLine + newLine);
        }
    }

    /**
     * Opens an arbitrary file via the local OS's default. If unable to open for
     * any reason, returns false.
     *
     * @param path
     * @return
     */
    @Override
    public boolean openFileNativeOS(String path) {
        boolean ret = true;

        try {
            File file = new File(path);
            Desktop.getDesktop().open(file);
        }
        catch (IOException e) {
            // internal logic based on thrown exception due to specific use case. No logging required.
            // IOHandler.writeErrorLog(e);
            ret = false;
        }

        return ret;
    }

    /**
     * Returns deepest directory from given path (truncating non-directory files
     * from the end)
     *
     * @param path path to fetch directory from
     * @return File representing directory, null if unable to capture directory
     * path for any reason
     */
    @Override
    public File getDirectoryFromPath(String path) {
        File ret = new File(path);

        if (ret.exists()) {
            while (ret != null && ret.exists() && !ret.isDirectory()) {
                ret = ret.getParentFile();
            }
        }

        if (ret != null && !ret.exists()) {
            ret = null;
        }

        return ret;
    }

    /**
     * Wraps File so that I can avoid importing it elsewhere in code
     *
     * @param path path to file
     * @return file
     */
    @Override
    public File getFileFromPath(String path) {
        return new File(path);
    }

    /**
     * Writes to the PolyGlot error log file
     *
     * @param exception
     */
    @Override
    public void writeErrorLog(Throwable exception) {
        writeErrorLog(exception, "");
    }

    /**
     * Writes to the PolyGlot error log file
     *
     * @param exception
     * @param comment
     */
    @Override
    public void writeErrorLog(Throwable exception, String comment) {
        String curContents = "";
        String errorMessage = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
        errorMessage += "-" + exception.getLocalizedMessage() + "-" + exception.getClass().getName();
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        rootCause = rootCause == null ? exception : rootCause;
        errorMessage += "\n" + ExceptionUtils.getStackTrace(rootCause);

        if (!comment.isEmpty()) {
            errorMessage = comment + ":\n" + errorMessage;
        }

        File errorLog = new File(PGTUtil.getErrorDirectory().getAbsolutePath()
                + File.separator + PGTUtil.ERROR_LOG_FILE);

        try {
            if (errorLog.exists()) {
                try ( Scanner logScanner = new Scanner(errorLog).useDelimiter("\\Z")) {
                    curContents = logScanner.hasNext() ? logScanner.next() : "";
                    
                    // wipe old system info if it still exists
                    if (curContents.contains(PGTUtil.ERROR_LOG_SPEARATOR)) {
                        curContents = curContents.substring(
                                curContents.indexOf(PGTUtil.ERROR_LOG_SPEARATOR) 
                                        + PGTUtil.ERROR_LOG_SPEARATOR.length());
                    }
                    
                    int length = curContents.length();
                    int newLength = length + errorMessage.length();

                    if (newLength > PGTUtil.MAX_LOG_CHARACTERS) {
                        curContents = curContents.substring(newLength - PGTUtil.MAX_LOG_CHARACTERS);
                    }
                }
            }

            try ( BufferedWriter writer = new BufferedWriter(new FileWriter(errorLog))) {
                String output = getSystemInformation() + PGTUtil.ERROR_LOG_SPEARATOR
                        + curContents + errorMessage + "\n";
//                System.out.println("Writing error to: " + errorLog.getAbsolutePath());
                writer.write(output);
            }
        } catch (IOException e) {
            // Fail silently. This fails almost exclusively due to being run in write protected folder, caught elsewhere
            // do not log to written file for obvious reasons (causes further write failure)
            // WHY DO PEOPLE INSTALL THIS TO WRITE PROTECTED FOLDERS AND SYSTEM32. WHY.
            // IOHandler.writeErrorLog(e);
        }
    }

    @Override
    public File getErrorLogFile() {
        return new File(PGTUtil.getErrorDirectory().getAbsolutePath()
                + File.separator + PGTUtil.ERROR_LOG_FILE);
    }

    @Override
    public String getErrorLog() throws FileNotFoundException {
        String ret = "";
        File errorLog = getErrorLogFile();

        if (errorLog.exists()) {
            try ( Scanner logScanner = new Scanner(errorLog).useDelimiter("\\Z")) {
                ret = logScanner.hasNext() ? logScanner.next() : "";
            }
        }
        return ret;
    }

    /**
     * Gets system information in human readable format
     *
     * @return system information
     */
    @Override
    public String getSystemInformation() {
        List<String> attributes = Arrays.asList("java.version",
                "java.vendor",
                "java.vendor.url",
                "java.vm.specification.version",
                "java.vm.specification.name",
                "java.vm.version",
                "java.vm.vendor",
                "java.vm.name",
                "java.specification.version",
                "java.specification.vendor",
                "java.specification.name",
                "java.class.version",
                "java.ext.dirs",
                "os.name",
                "os.arch",
                "os.version");
        String ret = "";

        for (String attribute : attributes) {
            ret += attribute + " : " + System.getProperty(attribute) + "\n";
        }

        return ret;
    }

    @Override
    public File unzipResourceToTempLocation(String resourceLocation) throws IOException {
        Path tmpPath = Files.createTempDirectory(PGTUtil.DISPLAY_NAME);
        unzipResourceToDir(resourceLocation, tmpPath);
        return tmpPath.toFile();
    }

    /**
     * Unzips an internal resource to a targeted path.Does not check header.
     *
     * @param internalPath Path to internal zipped resource
     * @param target destination to unzip to
     * @throws java.io.IOException
     */
    @Override
    public void unzipResourceToDir(String internalPath, Path target) throws IOException {
        InputStream fin = IOHandler.class.getResourceAsStream(internalPath);
        try ( ZipInputStream zin = new ZipInputStream(fin)) {
            unZipStreamToLocation(zin, target);
        }
    }

    public void unzipFileToDir(String archivePath, Path target) throws IOException {
        var fin = new FileInputStream(new File(archivePath));
        try ( var zin = new ZipInputStream(fin)) {
            File targetDir = new File(target.toString());
            if (!targetDir.exists()) {
                targetDir.mkdir();
            }
            unZipStreamToLocation(zin, target);
        }
    }

    /**
     * Unzips zipstream to particular location, preserving file/directory
     * structure
     *
     * @param zin
     * @param target
     * @throws IOException
     */
    private static void unZipStreamToLocation(ZipInputStream zin, Path target) throws IOException {
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            File extractTo = new File(target + File.separator + ze.getName());
            if (ze.isDirectory()) {
                extractTo.mkdir();
            } else {
                extractTo.createNewFile();
                try ( FileOutputStream out = new FileOutputStream(extractTo)) {
                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = zin.read(data, 0, data.length)) != -1) {
                        out.write(data, 0, nRead);
                    }
                }
            }
        }
    }
    
    /**
     * Archives directory (used for packing pgt files manually)
     * When packing a pgt file from directory, set preserveBaseDir to false
     * 
     * @param directoryPath
     * @param targetPath 
     * @param packingPgdFile Whether This is being used to pack a PGD file
     * @throws java.io.IOException 
     */
    public static void packDirectoryToZip(String directoryPath, String targetPath, boolean packingPgdFile) throws Exception {
        String sourceFile = directoryPath;
        try (FileOutputStream fos = new FileOutputStream(targetPath); 
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            File fileToZip = new File(sourceFile);
            zipFile(fileToZip, fileToZip.getName(), zipOut, !packingPgdFile);
        }
        
        // if packing PGD file, test readability afterward
        if (packingPgdFile) {
            // pass null shell class because this will ultimately be discarded
            DesktopHelpHandler helpHandler = new DesktopHelpHandler();
            PFontHandler fontHandler = new PFontHandler();
            var osHandler = new DesktopOSHandler(DesktopIOHandler.getInstance(), new DummyInfoBox(), helpHandler, fontHandler);
            DictCore test = new DictCore(new DesktopPropertiesManager(), osHandler, new PGTUtil(), new DesktopGrammarManager());
            PolyGlot.getTestShell(test);
            test.readFile(targetPath);
        }
    }
    
    /**
     * Recursing method to zip files/directors while preserving structure
     * 
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @param preserveBaseDir Whether to preserve th base directory or to add its CONTENTS to the root
     * @throws IOException 
     */
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut, boolean preserveBaseDir) throws IOException {
        if (!fileToZip.isHidden()) { // ignore files added by OS, they are garbo to PolyGlot
            if (fileToZip.isDirectory()) {
                if (preserveBaseDir) {
                    if (!fileName.endsWith("/")) {
                        fileName += "/";
                    }
                    
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    fileName = "";
                }
                
                File[] children = fileToZip.listFiles();
                for (var childFile : children) {
                    zipFile(childFile, fileName + childFile.getName(), zipOut, true);
                }
            } else { 
                try (FileInputStream fis = new FileInputStream(fileToZip)) { // handles normal files
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOut.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        }
    }

    /**
     * Adds a metadata attribute to an OSX file
     *
     * @param filePath
     * @param attribute
     * @param value
     * @param isHexVal
     * @throws Exception if you try to run it on a nonOSX platform
     */
    @Override
    public void addFileAttributeOSX(String filePath, String attribute, String value, boolean isHexVal) throws Exception {
        if (!PGTUtil.IS_OSX) {
            throw new Exception("This method may only be called within OSX.");
        }

        String writeMode = isHexVal ? "-wx" : "-w";
        String[] cmd = {"xattr", writeMode, attribute, value, filePath};
        String[] result = runAtConsole(cmd, false);

        if (!result[1].isBlank()) {
            throw new Exception(result[1]);
        }
    }

    /**
     * Runs a command at the console, returning informational and error output.
     *
     * @param arguments command to run as [0], with arguments following
     * @param addSpaces whether blank string argument values should be changed
     * to a single space (some OSes will simply ignore arguments that are empty)
     * @return String array with two entries. [0] = Output, [1] = Error Output
     */
    @Override
    public String[] runAtConsole(String[] arguments, boolean addSpaces) {
        String output = "";
        String error = "";

        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].isEmpty()) {
                arguments[i] = " ";
            }
        }

        try {
            Process p = Runtime.getRuntime().exec(arguments);

            // get general output
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));  BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output += line;
                }

                while ((line = errorReader.readLine()) != null) {
                    error += line;
                }
            }
        } catch (IOException e) {
            error = e.getLocalizedMessage();
        }

        return new String[]{output, error};
    }

    /**
     * Does what it says on the tin.Clear those carriage returns away.
     *
     * @param filthyWithWindows
     * @return
     */
    @Override
    public byte[] clearCarrigeReturns(byte[] filthyWithWindows) {
        byte[] ret = new byte[filthyWithWindows.length];
        int cleanCount = 0;

        for (byte test : filthyWithWindows) {
            if (test != 13) {
                ret[cleanCount] = test;
                cleanCount++;
            }
        }

        return Arrays.copyOfRange(ret, 0, cleanCount);
    }

    /**
     * Pulls in new image from user selected file Returns null if user cancels
     * process
     *
     * @param parent parent form
     * @param workingDirectory
     * @return ImageNode inserted into collection with populated image
     * @throws IOException on file read error
     */
    public ImageNode openNewImage(Window parent, File workingDirectory) throws Exception {
        ImageNode image = null;
        try {
            DictCore core = PolyGlot.getPolyGlot().getCore();
            BufferedImage buffImg = openImage(parent, workingDirectory);

            if (buffImg != null) {
                image = new ImageNode(core);
                image.setImageBytes(loadImageBytesFromImage(buffImg));
                core.getImageCollection().insert(image);
            }
        }
        catch (IOException e) {
            throw new IOException("Problem loading image: " + e.getLocalizedMessage(), e);
        }
        return image;
    }

    @Override
    public byte[] loadImageBytes(String path) throws IOException {
        ImageIcon loadBlank = new ImageIcon(getClass().getResource(path));
        BufferedImage image = new BufferedImage(
                loadBlank.getIconWidth(),
                loadBlank.getIconHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics g = image.createGraphics();

        loadBlank.paintIcon(null, g, 0, 0);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        return baos.toByteArray();
    }

    public byte[] loadImageBytesFromImage(Image img) throws IOException {
        BufferedImage image = PGTUtil.toBufferedImage(img);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * Takes a buffered image, and returns a node containing it, having inserted
     * the node with ID to persist on save
     *
     * @param _image Image to get node of.
     * @return populated Image node
     * @throws java.lang.Exception
     */
    public ImageNode getFromBufferedImage(BufferedImage _image) throws Exception {
        DictCore core = PolyGlot.getPolyGlot().getCore();
        ImageNode ret = new ImageNode(core);
        ret.setImageBytes(loadImageBytesFromImage(_image));
        core.getImageCollection().insert(ret);

        return ret;
    }

    public static DesktopIOHandler getInstance() {
        if (ioHandler == null) {
            ioHandler = new DesktopIOHandler();
        }

        return ioHandler;
    }

    private DesktopIOHandler() {
    }
}

/*
 * Copyright (c) 2019, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: Creative Commons Attribution-NonCommercial 4.0 International Public License
 *  See LICENSE.TXT included with this code to read the full license agreement.

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
package org.darisadesigns.polyglotlina.Nodes;

import TestResources.DummyCore;
import TestResources.TestResources;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.darisadesigns.polyglotlina.CustomControls.PAlphaMap;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author draque
 */
public class ConWordTest {
    private final ConWord word;
    private final DictCore core;
    
    public ConWordTest() {
        word = new ConWord();
        core = DummyCore.newCore();
        word.setCore(core);
        word.setParent(core.getWordCollection());

        try {
            WordClass textClass = new WordClass();
            textClass.setFreeText(true);
            core.getWordClassCollection().addNode(textClass);

            WordClass normalClass = new WordClass();
            normalClass.addValue("VAL1");
            normalClass.addValue("VAL2");
            normalClass.addValue("VAL3");
            core.getWordClassCollection().addNode(normalClass);

            for (WordClass addClass : core.getWordClassCollection().getAllWordClasses()) {
                if (addClass.isFreeText()) {
                    word.setClassTextValue(addClass.getId(), "CLASS_TEXT_VAL!");
                } else {
                    word.setClassValue(addClass.getId(), 2);
                }
            }

            word.setDefinition("DEFINITION");
            word.setEtymNotes("ETYMOLOGY NOTES");
            word.setLocalWord("LOCALWORD!");
            word.setProcOverride(true);
            word.setPronunciation("PRONUNCIATION");
            word.setValue("MY_WORD");
            word.setWordTypeId(13);
        } catch (Exception e) {
            fail(e);
        }
    }

//    @Test
//    public void testSetEqual() {
//        ConWord copy = new ConWord();
//        copy.setCore(core);
//        copy.setEqual(word);
//        assertEquals(copy, word);
//    }

    @Test
    public void testEquals() {
        ConWord copy = new ConWord();
        copy.setCore(core);
        copy.setParent(core.getWordCollection());

        for (WordClass addClass : core.getWordClassCollection().getAllWordClasses()) {
            if (addClass.isFreeText()) {
                copy.setClassTextValue(addClass.getId(), "CLASS_TEXT_VAL!");
            } else {
                copy.setClassValue(addClass.getId(), 2);
            }
        }

        copy.setDefinition("DEFINITION");
        copy.setEtymNotes("ETYMOLOGY NOTES");
        copy.setLocalWord("LOCALWORD!");
        copy.setProcOverride(true);
        copy.setPronunciation("PRONUNCIATION");
        copy.setValue("MY_WORD");
        copy.setWordTypeId(13);
        
        assertEquals(copy, word);
    }
    
    @Test
    public void testNotEquals() {
        ConWord copy = new ConWord();
        copy.setCore(core);
        copy.setEqual(word);
        copy.setClassValue(4, 0);
        assertNotEquals(copy, word);
    }

    // TODO: Maybe test this elsewhere, as this touches functionality from most node types...
//    @Test
//    public void testCheckValid() {
//    }
    
    @Test
    public void testCompareTo() {
        ConWord before = new ConWord();
        ConWord after = new ConWord();

        PAlphaMap alphaOrder = new PAlphaMap();
        alphaOrder.put("b", 0);
        alphaOrder.put("a", 1);

        before.setValue("baaaa");
        after.setValue("aaaaa");
        before.setAlphaOrder(alphaOrder);

        List<ConWord> orderMe = Arrays.asList(after, before);

        // out of order initially...
        assertEquals(orderMe.get(0), after);

        Collections.sort(orderMe);

        // ordered after
        assertEquals(orderMe.get(0), before);
    }

    @Test
    public void testWriteXML() {
        System.out.println("ConWordTest:testWriteXML");
        String expectedValue = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                + "<dictionary>"
                + "<word>"
                + "<wordId>0</wordId>"
                + "<localWord>LOCALWORD!</localWord>"
                + "<conWord>MY_WORD</conWord>"
                + "<wordTypeId>13</wordTypeId>"
                + "<pronunciation>PRONUNCIATION</pronunciation>"
                + "<definition>DEFINITION</definition>"
                + "<wordProcOverride>T</wordProcOverride>"
                + "<autoDeclOverride>F</autoDeclOverride>"
                + "<wordRuleOverride>F</wordRuleOverride>"
                + "<wordClassCollection>"
                + "<wordClassification>3,2</wordClassification>"
                + "</wordClassCollection>"
                + "<wordClassTextValueCollection>"
                + "<wordClassTextValue>2,CLASS_TEXT_VAL!</wordClassTextValue>"
                + "</wordClassTextValueCollection>"
                + "<wordEtymologyNotes>ETYMOLOGY NOTES</wordEtymologyNotes>"
                + "</word></dictionary>";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(PGTUtil.DICTIONARY_XID);
            doc.appendChild(rootElement);

            word.writeXML(doc, rootElement);

            assertTrue(TestResources.textXmlDocEquals(doc, expectedValue));
        } catch (IOException | ParserConfigurationException | TransformerException | DOMException e) {
            fail(e);
        }
    }
}
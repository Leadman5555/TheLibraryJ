package org.library.thelibraryj.infrastructure.textParsers.fileParsers.domain;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.NodeList;

class OdtParser {

    @Nullable
    public static String parseOdtTextFile(MultipartFile textFile) {
        try(OdfTextDocument document = OdfTextDocument.loadDocument(textFile.getInputStream())) {
            StringBuilder stringBuilder = new StringBuilder();
            NodeList paragraphs = document.getContentRoot().getElementsByTagName("text:p");
            int paragraphCount = paragraphs.getLength()-1;
            for (int i = 0; i < paragraphCount; i++) {
                stringBuilder.append(paragraphs.item(i).getTextContent());
                stringBuilder.append('\n');
            }
            return stringBuilder.append(paragraphs.item(paragraphCount).getTextContent()).toString();
        } catch (Exception e) {
            return null;
        }
    }
}

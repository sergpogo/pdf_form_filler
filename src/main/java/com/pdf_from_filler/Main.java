package com.pdf_from_filler;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main
{
    public static void main(String[] args) throws DocumentException, IOException
    {
        if (args.length == 0 || (args.length == 1 && args[0] == "-h")) {
            System.out.println("Usage: 6 or 7 arguments: source, JSON data, font, dest, flatten, facsimile data, font size = 10");
            System.exit(0);
        }
        else if (args.length != 6 && args.length != 7) {
            System.err.println("Wrong number of arguments");
            System.exit(1);
        }

        String font_size = args.length == 6 ? "10" : args[6];
        Boolean flatten = Boolean.valueOf(args[4]);

        new Main().manipulatePdf(args[0], args[1], args[2], args[3], flatten, args[5], font_size);
    }


    public void manipulatePdf(String src, String data, String font, String dest, Boolean flatten, String facsimileData, String font_size) throws DocumentException, IOException
    {
        File file = new File(dest);

        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();

        ObjectMapper mapper = new ObjectMapper();
        File from = new File(data);
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        HashMap<String, String> o = mapper.readValue(from, typeRef);
        
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        AcroFields fields = stamper.getAcroFields();

        fields.setGenerateAppearances(true);

        BaseFont bf = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, null, null, false);

        for(Map.Entry<String, String> entry : o.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            fields.setFieldProperty(key, "textsize", Float.valueOf(font_size), null);
            fields.setFieldProperty(key, "textfont", bf, null);
            fields.setField(key, value);
        }
        
        File facsimileFile = new File(facsimileData);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Facsimile> facsimileArray = mapper.readValue(facsimileFile, new TypeReference<List<Facsimile>>(){});

        for (Facsimile facsimile : facsimileArray) {
            setFacsimileImage(facsimile, stamper);
        }
        
        if (flatten == true)
        	stamper.setFormFlattening(true);
        
        stamper.close();
        reader.close();

    }
    
    private void setFacsimileImage(Facsimile facsimile, PdfStamper stamper) throws IOException, DocumentException {
        Image image = Image.getInstance(facsimile.getImgPath());
        PdfImage stream = new PdfImage(image, "", null);
        stream.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
        image.setDirectReference(ref.getIndirectReference());
        image.setAbsolutePosition(facsimile.getAbsolutePositionX(), facsimile.getAbsolutePositionY());
        image.scaleToFit(facsimile.getWidth(), facsimile.getHeight());
        PdfContentByte over = stamper.getOverContent(facsimile.getPageNumber());
        over.addImage(image);
    }

}
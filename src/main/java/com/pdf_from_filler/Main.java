package com.pdf_from_filler;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main
{
    public static void main(String[] args) throws DocumentException, IOException
    {
        if (args.length == 0 || (args.length == 1 && args[0] == "-h")) {
            System.out.println("Usage: 5 or 6 arguments: source, JSON data, font, dest, flatten, font size = 10");
            System.exit(0);
        }
        else if (args.length != 5 && args.length != 6) {
            System.err.println("Wrong number of arguments");
            System.exit(1);
        }

        String font_size = args.length == 5 ? "10" : args[5];
        Boolean flatten = Boolean.valueOf(args[4]);

        new Main().manipulatePdf(args[0], args[1], args[2], args[3], flatten, font_size);
    }



    public void manipulatePdf(String src, String data, String font, String dest, Boolean flatten, String font_size) throws DocumentException, IOException
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

        if (flatten == true)
        	stamper.setFormFlattening(true);
        
        stamper.close();

    }

}
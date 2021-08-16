package com.wwd.tgdb.util;

import com.wwd.tgdb.model.GPL.Category;
import com.wwd.tgdb.repository.CategoryRepository;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLCategoryHandler extends DefaultHandler {
    private CategoryRepository categoryRepository;
    String id, code, name, depth_level, sectionId, sectionCode, lastElementName;

    public XMLCategoryHandler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        lastElementName = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String information = new String(ch, start, length);

        if (!information.isEmpty()) {
            if (lastElementName.equals("id"))
                id = information;
            if (lastElementName.equals("code"))
                code = information;
            if (lastElementName.equals("name"))
                name = information;
//            if (lastElementName.equals("section_id"))
//                sectionId = information;
//            if (lastElementName.equals("section_code"))
//                sectionCode = information;
            if (lastElementName.equals("depth_level"))
                depth_level = information;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        try {
            if (lastElementName.equals("depth_level") && depth_level != null) {
                if (depth_level.equals("3")) {

//                    System.out.println(id +
//                            " - " + code +
//                            " - " + name +
//                            " - " + sectionId +
//                            " - " + sectionCode +
//                            " - " + depthLevel);

                    if (!categoryRepository.existsByName(name)) {
                        Category category = new Category();
                        category.setName(name);
                        category.setSectionId(code);
                        categoryRepository.save(category);
                    }
                }
                id = null;
                code = null;
                name = null;
                depth_level = null;
            }
//            if (qName.equals("section_catalog")) {
//                System.out.println("SECTIONS - END");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

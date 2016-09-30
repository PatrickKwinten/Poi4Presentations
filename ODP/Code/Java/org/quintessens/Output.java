package org.quintessens;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.Document;

import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.webapp.XspHttpServletResponse;

import ch.hasselba.napi.NAPIUtils;

public class Output {

    NotesContext nct = NotesContext.getCurrent();
    Session session = nct.getCurrentSession();
    
    public void createPptx(String docId) {
        System.out.println(docId);
        try {

            Document doc = session.getCurrentDatabase().getDocumentByUNID(docId);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            System.out.println(doc.getUniversalID());

            XMLSlideShow ppt;
            try {
                ppt = new XMLSlideShow( NAPIUtils.loadBinaryFile( session.getServerName(),session.getCurrentDatabase().getFilePath(), "Duffbeers.pptx"));
                //System.out.println(ppt.getProperties());                
                int slideCounter = ppt.getSlides().size();
                System.out.println("contains " + slideCounter + " number of slides");
                // first see what slide layouts are available :
                System.out.println("Available slide layouts:");
                for (XSLFSlideMaster master: ppt.getSlideMasters()) {
                    for (XSLFSlideLayout layout: master.getSlideLayouts()) {
                        System.out.println(layout.getType());
                    }
                }

                XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
                XSLFSlideLayout defaultLayout = defaultMaster.getLayout(SlideLayout.BLANK);

                /*
                 * Add a slide based on TITLE_ONLY layout
                 */
                defaultLayout = defaultMaster.getLayout(SlideLayout.TITLE_ONLY);
                XSLFSlide slide01 = ppt.createSlide(defaultLayout);
                XSLFTextShape title01 = slide01.getPlaceholder(0);
                title01.setText(doc.getItemValueString("refName"));

                /*
                 *    Add a slide based on TITLE_AND_CONTENT layout                       
                 */
                defaultLayout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
                XSLFSlide slide02 = ppt.createSlide(defaultLayout);
                XSLFTextShape title02 = slide02.getPlaceholder(0);
                title02.setText("Strength");
                XSLFTextShape body02 = slide02.getPlaceholder(1);
                Vector < String > strength = doc.getItemValue("refStrength");
                for (int i = 0; i < strength.size(); i++) {
                    body02.addNewTextParagraph().addNewTextRun().setText(strength.get(i));
                }

                /*
                 *    Add another slide based on TITLE_AND_CONTENT layout                       
                 */
                XSLFSlide slide03 = ppt.createSlide(defaultLayout);
                XSLFTextShape title03 = slide03.getPlaceholder(0);
                title03.setText("Weakness");
                XSLFTextShape body03 = slide03.getPlaceholder(1);
                Vector <String> weak = doc.getItemValue("refWeakness");
                for (int i = 0; i < weak.size(); i++) {
                    body03.addNewTextParagraph().addNewTextRun().setText(weak.get(i));
                }
                
                /*
                 *    Add another slide based on TEXT layout                       
                 */                
                defaultLayout = defaultMaster.getLayout(SlideLayout.TEXT);
                XSLFSlide slide04 = ppt.createSlide(defaultLayout);
                XSLFTextShape title04 = slide04.getPlaceholder(0);                
                title04.setText("Link(s) of interest");	
                XSLFTextShape body04 = slide04.getPlaceholder(1);                
                body04.clearText();
              
                XSLFTextRun textRunLine2 = body04.addNewTextParagraph().addNewTextRun();
                textRunLine2.setText(doc.getItemValueString("refLinkLabel"));	
                XSLFHyperlink link04 = textRunLine2.createHyperlink();
                link04.setAddress(doc.getItemValueString("refLinkURL"));
                
                // 0-based index of a slide to be removed
                for (int i = 0; i < slideCounter; i++) {
                    ppt.removeSlide(0);
                }

                String filename = doc.getItemValueString("refName") + ".pptx";

                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ex = fc.getExternalContext();
                XspHttpServletResponse response = (XspHttpServletResponse) ex.getResponse();

                try {
                    ServletOutputStream writer = response.getOutputStream();
                    response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Content-Disposition", "inline; filename=" + filename);
                    response.setDateHeader("Expires", -1);
                    ppt.write(writer);
                    writer.flush();
                    writer.close();
                    fc.responseComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (NotesException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            doc.recycle();

        } catch (NotesException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
    }
}
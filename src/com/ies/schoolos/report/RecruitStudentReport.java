package com.ies.schoolos.report;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.container.DbConnection;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.recruit.RecruitStudentFamilySchema;
import com.ies.schoolos.schema.recruit.RecruitStudentSchema;
import com.ies.schoolos.type.AliveStatus;
import com.ies.schoolos.type.Blood;
import com.ies.schoolos.type.ClassRange;
import com.ies.schoolos.type.FamilyStatus;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.GuardianRelation;
import com.ies.schoolos.type.Nationality;
import com.ies.schoolos.type.Occupation;
import com.ies.schoolos.type.PeopleIdType;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.Race;
import com.ies.schoolos.type.Religion;
import com.ies.schoolos.utility.DateTimeUtil;
import com.ies.schoolos.utility.EmailSender;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

public class RecruitStudentReport {
	
	private final String DEFAULT_FORM = "https://storage.googleapis.com/schoolos/forms/";
	private final String DEFAULT_IMAGE = "https://storage.googleapis.com/schoolos/images/";
	private final String REPORT_ID = "recruit_student.jasper";
	private final String LOGO = UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_ID) +".jpg";
		
	private StreamResource resource;
	private String fileName = "";
	private String emailTo = "";
	
	public RecruitStudentReport(int studentId) {
		printReport(studentId);
	}
	
	public RecruitStudentReport(int studentId, boolean emailMode) {
		printReport(studentId);
		if(emailMode)
			sendEmail();
	}
	
	@SuppressWarnings("deprecation")
	public void printReport(int studentId){
		try {
			final Connection con = DbConnection.getConnection().reserveConnection();
			final Map<String, Object> paramMap = new HashMap<String, Object>();
			Item studentItem = Container.getInstance().getRecruitStudentContainer().getItem(new RowId(studentId));
			
			contertoMap(paramMap, studentItem);
			
			StreamResource.StreamSource source = new StreamResource.StreamSource() {
				private static final long serialVersionUID = 1L;

				public InputStream getStream() {
						byte[] b = null;
						try {
			            InputStream rep = new URL(DEFAULT_FORM + REPORT_ID).openStream();
			            //InputStream rep = new FileInputStream(new File("C:\\Users\\Sharif\\Desktop\\Report\\recruit_student.jasper"));
			            
			            getImage(paramMap);
			    		
			            if (rep!=null) {
			              JasperReport report = (JasperReport) JRLoader.loadObject(rep);
			              report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
			              b = JasperRunManager.runReportToPdf(report, paramMap, con);
			            }
			          } catch (Exception ex) {
			            Logger.getLogger(RecruitStudentReport.class.getName()).log(Level.SEVERE, null, ex);
		
			          }
		          return new ByteArrayInputStream(b);  
				}
			};
			
		    resource = new StreamResource(source, studentItem.getItemProperty(RecruitStudentSchema.PEOPLE_ID) + ".pdf");
		    Page.getCurrent().open(resource, "_blank",false);
		    
	        DbConnection.getConnection().releaseConnection(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* ส่งอีเมลล์ใบสมัคร */
	private void sendEmail(){
		String subject = "ใบสมัคร"+UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_NAME).toString();
		String description = "อีเมล์ฉบับนี้ส่งมา เพื่อยืนยันว่าคุณได้สมัครเรียนเรียบร้อยแล้ว สำหรับ" + UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_NAME).toString() + System.getProperty("line.separator") +
				"กรุณาพิมพ์หลักฐานการสมัครที่แนบด้านล่างนี้ ในการประกอบเอกสารการรับสมัคร" + System.getProperty("line.separator") +
				"หากมีข้อสงสัยกรุณาติดต่อกลับที่ " + UI.getCurrent().getSession().getAttribute(SessionSchema.EMAIL).toString() + System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"กรุณาอย่าส่งกลับมาอีเมลล์ที่ใช้ในการส่งนี้ " + System.getProperty("line.separator") +
				"โดย ทีมงาน SchoolOS ";
		
		
		new EmailSender(emailTo,subject,description,fileName, resource.getStream().getStream());	   
	}
	
	/* ดึงรูปภาพมาแสดง */
	private void getImage(Map<String, Object> paramMap){
		 BufferedImage image;
		try {
			image = ImageIO.read(new URL(DEFAULT_IMAGE + LOGO).openStream());
			paramMap.put("logo", image );
		}catch (Exception e) {
			try {
				image = ImageIO.read(new URL(DEFAULT_IMAGE + "default.png").openStream());
				paramMap.put("logo", image );
			}catch (Exception e2){
				paramMap.put("logo", "");
			}
		}
	}
	
	private void contertoMap(Map<String, Object> paramMap, Item item){
		/* นักเรียน */
		String[] regDate = DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(RecruitStudentSchema.REGISTER_DATE).getValue().toString()).split("/");
		
		fileName = item.getItemProperty(RecruitStudentSchema.PEOPLE_ID).getValue().toString();
		emailTo = item.getItemProperty(RecruitStudentSchema.EMAIL).getValue().toString();
		
		paramMap.put("reg_id",item.getItemProperty(RecruitStudentSchema.RECRUIT_CODE).getValue());
		paramMap.put("school_name",UI.getCurrent().getSession().getAttribute(SessionSchema.SCHOOL_NAME));
		paramMap.put(RecruitStudentSchema.STUDENT_ID,item.getItemProperty(RecruitStudentSchema.STUDENT_ID).getValue());
		paramMap.put(RecruitStudentSchema.CLASS_RANGE,ClassRange.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CLASS_RANGE).getValue().toString())));
		paramMap.put(RecruitStudentSchema.PEOPLE_ID,item.getItemProperty(RecruitStudentSchema.PEOPLE_ID).getValue());
		paramMap.put(RecruitStudentSchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PEOPLE_ID_TYPE).getValue().toString())));
		paramMap.put(RecruitStudentSchema.PRENAME,Prename.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString())));
		paramMap.put(RecruitStudentSchema.FIRSTNAME,item.getItemProperty(RecruitStudentSchema.FIRSTNAME).getValue());
		paramMap.put(RecruitStudentSchema.LASTNAME,item.getItemProperty(RecruitStudentSchema.LASTNAME).getValue());
		paramMap.put(RecruitStudentSchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.PRENAME).getValue().toString())));
		paramMap.put(RecruitStudentSchema.FIRSTNAME_ND,item.getItemProperty(RecruitStudentSchema.FIRSTNAME_ND).getValue());
		paramMap.put(RecruitStudentSchema.LASTNAME_ND,item.getItemProperty(RecruitStudentSchema.LASTNAME_ND).getValue());
		paramMap.put(RecruitStudentSchema.NICKNAME,item.getItemProperty(RecruitStudentSchema.NICKNAME).getValue());
		paramMap.put(RecruitStudentSchema.GENDER,Gender.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GENDER).getValue().toString())));
		paramMap.put(RecruitStudentSchema.RELIGION,Religion.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.RELIGION).getValue().toString())));
		paramMap.put(RecruitStudentSchema.RACE,Race.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.RACE).getValue().toString())));
		paramMap.put(RecruitStudentSchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.NATIONALITY).getValue().toString())));
		paramMap.put(RecruitStudentSchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(RecruitStudentSchema.BIRTH_DATE).getValue().toString()));
		paramMap.put(RecruitStudentSchema.BLOOD,Blood.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.BLOOD).getValue().toString())));
		paramMap.put(RecruitStudentSchema.HEIGHT,item.getItemProperty(RecruitStudentSchema.HEIGHT).getValue());
		paramMap.put(RecruitStudentSchema.WEIGHT,item.getItemProperty(RecruitStudentSchema.WEIGHT).getValue());
		paramMap.put(RecruitStudentSchema.CONGENITAL_DISEASE,item.getItemProperty(RecruitStudentSchema.CONGENITAL_DISEASE).getValue());
		paramMap.put(RecruitStudentSchema.INTERESTED,item.getItemProperty(RecruitStudentSchema.INTERESTED).getValue());
		paramMap.put(RecruitStudentSchema.SIBLING_QTY,item.getItemProperty(RecruitStudentSchema.SIBLING_QTY).getValue());
		paramMap.put(RecruitStudentSchema.SIBLING_SEQUENCE,item.getItemProperty(RecruitStudentSchema.SIBLING_SEQUENCE).getValue());
		paramMap.put(RecruitStudentSchema.SIBLING_INSCHOOL_QTY,item.getItemProperty(RecruitStudentSchema.SIBLING_INSCHOOL_QTY).getValue());
		paramMap.put(RecruitStudentSchema.GRADUATED_SCHOOL,item.getItemProperty(RecruitStudentSchema.GRADUATED_SCHOOL).getValue());
		paramMap.put(RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID,getProvinceName(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GRADUATED_SCHOOL_PROVINCE_ID).getValue().toString())));
		paramMap.put(RecruitStudentSchema.GRADUATED_GPA,item.getItemProperty(RecruitStudentSchema.GRADUATED_GPA).getValue());
		paramMap.put(RecruitStudentSchema.GRADUATED_YEAR,item.getItemProperty(RecruitStudentSchema.GRADUATED_YEAR).getValue());
		paramMap.put(RecruitStudentSchema.TEL,item.getItemProperty(RecruitStudentSchema.TEL).getValue());
		paramMap.put(RecruitStudentSchema.MOBILE,item.getItemProperty(RecruitStudentSchema.MOBILE).getValue());
		paramMap.put(RecruitStudentSchema.EMAIL,item.getItemProperty(RecruitStudentSchema.EMAIL).getValue());
		paramMap.put(RecruitStudentSchema.CURRENT_ADDRESS,item.getItemProperty(RecruitStudentSchema.CURRENT_ADDRESS).getValue());
		paramMap.put(RecruitStudentSchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CURRENT_CITY_ID).getValue().toString())));
		paramMap.put(RecruitStudentSchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CURRENT_DISTRICT_ID).getValue().toString())));
		paramMap.put(RecruitStudentSchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CURRENT_PROVINCE_ID).getValue().toString())));
		paramMap.put(RecruitStudentSchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.CURRENT_POSTCODE_ID).getValue().toString())));
		paramMap.put(RecruitStudentSchema.FAMILY_STATUS,FamilyStatus.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.FAMILY_STATUS).getValue().toString())));
		paramMap.put(RecruitStudentSchema.GUARDIAN_RELATION,GuardianRelation.getNameTh(Integer.parseInt(item.getItemProperty(RecruitStudentSchema.GUARDIAN_RELATION).getValue().toString())));
		paramMap.put(RecruitStudentSchema.REG_DATE,regDate[0]);
		paramMap.put(RecruitStudentSchema.REG_MONTH,regDate[1]);
		paramMap.put(RecruitStudentSchema.REG_YEAR,regDate[2]);
		
		/* FATHER */
		Object fatherId = item.getItemProperty(RecruitStudentSchema.FATHER_ID).getValue();
		Item fatherItem = Container.getInstance().getRecruitFamilyContainer().getItem(new RowId(fatherId));
		
		paramMap.put("f_" + RecruitStudentFamilySchema.REG_FAMILY_ID,fatherItem.getItemProperty(RecruitStudentFamilySchema.REG_FAMILY_ID).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.PEOPLE_ID,fatherItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID_TYPE).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.PRENAME,Prename.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.PRENAME).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.FIRSTNAME,fatherItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.LASTNAME,fatherItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.PRENAME).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.FIRSTNAME_ND,fatherItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME_ND).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.LASTNAME_ND,fatherItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME_ND).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.GENDER,Gender.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.GENDER).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.RELIGION,Religion.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.RELIGION).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.RACE,Race.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.RACE).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.NATIONALITY).getValue().toString())));
		if(fatherItem.getItemProperty(RecruitStudentFamilySchema.BIRTH_DATE).getValue() != null)
			paramMap.put("f_" + RecruitStudentFamilySchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(fatherItem.getItemProperty(RecruitStudentFamilySchema.BIRTH_DATE).getValue().toString()));
		paramMap.put("f_" + RecruitStudentFamilySchema.TEL,fatherItem.getItemProperty(RecruitStudentFamilySchema.TEL).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.MOBILE,fatherItem.getItemProperty(RecruitStudentFamilySchema.MOBILE).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.EMAIL,fatherItem.getItemProperty(RecruitStudentFamilySchema.EMAIL).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.SALARY,fatherItem.getItemProperty(RecruitStudentFamilySchema.SALARY).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.ALIVE_STATUS,AliveStatus.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.ALIVE_STATUS).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.OCCUPATION,Occupation.getNameTh(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.OCCUPATION).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.JOB_ADDRESS,fatherItem.getItemProperty(RecruitStudentFamilySchema.JOB_ADDRESS).getValue());
		paramMap.put("f_" + RecruitStudentFamilySchema.CURRENT_ADDRESS,fatherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_ADDRESS).getValue());		
		paramMap.put("f_" + RecruitStudentFamilySchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_CITY_ID).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_DISTRICT_ID).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_PROVINCE_ID).getValue().toString())));
		paramMap.put("f_" + RecruitStudentFamilySchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(fatherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_POSTCODE_ID).getValue().toString())));
		
		/* Mother */
		Object motherId = item.getItemProperty(RecruitStudentSchema.MOTHER_ID).getValue();
		Item motherItem = Container.getInstance().getRecruitFamilyContainer().getItem(new RowId(motherId));
		
		paramMap.put("m_" + RecruitStudentFamilySchema.REG_FAMILY_ID,motherItem.getItemProperty(RecruitStudentFamilySchema.REG_FAMILY_ID).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.PEOPLE_ID,motherItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID_TYPE).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.PRENAME,Prename.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.PRENAME).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.FIRSTNAME,motherItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.LASTNAME,motherItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.PRENAME).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.FIRSTNAME_ND,motherItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME_ND).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.LASTNAME_ND,motherItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME_ND).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.GENDER,Gender.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.GENDER).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.RELIGION,Religion.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.RELIGION).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.RACE,Race.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.RACE).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.NATIONALITY).getValue().toString())));
		if(motherItem.getItemProperty(RecruitStudentFamilySchema.BIRTH_DATE).getValue() != null)
			paramMap.put("m_" + RecruitStudentFamilySchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(motherItem.getItemProperty(RecruitStudentFamilySchema.BIRTH_DATE).getValue().toString()));
		paramMap.put("m_" + RecruitStudentFamilySchema.TEL,motherItem.getItemProperty(RecruitStudentFamilySchema.TEL).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.MOBILE,motherItem.getItemProperty(RecruitStudentFamilySchema.MOBILE).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.EMAIL,motherItem.getItemProperty(RecruitStudentFamilySchema.EMAIL).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.SALARY,motherItem.getItemProperty(RecruitStudentFamilySchema.SALARY).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.ALIVE_STATUS,AliveStatus.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.ALIVE_STATUS).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.OCCUPATION,Occupation.getNameTh(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.OCCUPATION).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.JOB_ADDRESS,motherItem.getItemProperty(RecruitStudentFamilySchema.JOB_ADDRESS).getValue());
		paramMap.put("m_" + RecruitStudentFamilySchema.CURRENT_ADDRESS,motherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_ADDRESS).getValue());		
		paramMap.put("m_" + RecruitStudentFamilySchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_CITY_ID).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_DISTRICT_ID).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_PROVINCE_ID).getValue().toString())));
		paramMap.put("m_" + RecruitStudentFamilySchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(motherItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_POSTCODE_ID).getValue().toString())));
				
		/* Guardian */
		Object guardianId = item.getItemProperty(RecruitStudentSchema.GUARDIAN_ID).getValue();
		Item guardianItem = Container.getInstance().getRecruitFamilyContainer().getItem(new RowId(guardianId));
		
		paramMap.put("g_" + RecruitStudentFamilySchema.REG_FAMILY_ID,guardianItem.getItemProperty(RecruitStudentFamilySchema.REG_FAMILY_ID).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.PEOPLE_ID,guardianItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.PEOPLE_ID_TYPE).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.PRENAME,Prename.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.PRENAME).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.FIRSTNAME,guardianItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.LASTNAME,guardianItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.PRENAME).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.FIRSTNAME_ND,guardianItem.getItemProperty(RecruitStudentFamilySchema.FIRSTNAME_ND).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.LASTNAME_ND,guardianItem.getItemProperty(RecruitStudentFamilySchema.LASTNAME_ND).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.GENDER,Gender.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.GENDER).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.RELIGION,Religion.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.RELIGION).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.RACE,Race.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.RACE).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.NATIONALITY).getValue().toString())));
		if(guardianItem.getItemProperty(RecruitStudentFamilySchema.BIRTH_DATE).getValue() != null)
			paramMap.put("g_" + RecruitStudentFamilySchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(guardianItem.getItemProperty(RecruitStudentFamilySchema.BIRTH_DATE).getValue().toString()));
		paramMap.put("g_" + RecruitStudentFamilySchema.TEL,guardianItem.getItemProperty(RecruitStudentFamilySchema.TEL).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.MOBILE,guardianItem.getItemProperty(RecruitStudentFamilySchema.MOBILE).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.EMAIL,guardianItem.getItemProperty(RecruitStudentFamilySchema.EMAIL).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.SALARY,guardianItem.getItemProperty(RecruitStudentFamilySchema.SALARY).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.ALIVE_STATUS,AliveStatus.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.ALIVE_STATUS).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.OCCUPATION,Occupation.getNameTh(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.OCCUPATION).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.JOB_ADDRESS,guardianItem.getItemProperty(RecruitStudentFamilySchema.JOB_ADDRESS).getValue());
		paramMap.put("g_" + RecruitStudentFamilySchema.CURRENT_ADDRESS,guardianItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_ADDRESS).getValue());		
		paramMap.put("g_" + RecruitStudentFamilySchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_CITY_ID).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_DISTRICT_ID).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_PROVINCE_ID).getValue().toString())));
		paramMap.put("g_" + RecruitStudentFamilySchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(guardianItem.getItemProperty(RecruitStudentFamilySchema.CURRENT_POSTCODE_ID).getValue().toString())));
		
	}

	private String getProvinceName(int itemId){
		SQLContainer provinceCon = Container.getInstance().getProvinceContainer();
		provinceCon.addContainerFilter(new Equal(ProvinceSchema.PROVINCE_ID,itemId));
		String name = provinceCon.getItem(new RowId(itemId)).getItemProperty(ProvinceSchema.NAME).getValue().toString();
		provinceCon.removeAllContainerFilters();
		
		return name;
	}

	private String getDistrictName(int itemId){
		SQLContainer districtCon = Container.getInstance().getDistrictContainer();
		districtCon.addContainerFilter(new Equal(DistrictSchema.DISTRICT_ID,itemId));
		String name = districtCon.getItem(new RowId(itemId)).getItemProperty(DistrictSchema.NAME).getValue().toString();
		districtCon.removeAllContainerFilters();
		
		return  name;
	}

	private String getCityName(int itemId){
		SQLContainer cityCon = Container.getInstance().getCityContainer();
		cityCon.addContainerFilter(new Equal(CitySchema.CITY_ID,itemId));
		String name = cityCon.getItem(new RowId(itemId)).getItemProperty(CitySchema.NAME).getValue().toString();
		cityCon.removeAllContainerFilters();
		
		return name;
	}

	private String getPostcodeName(int itemId){
		SQLContainer postcodeCon = Container.getInstance().getPostcodeContainer();
		postcodeCon.addContainerFilter(new Equal(PostcodeSchema.POSTCODE_ID,itemId));
		String code = postcodeCon.getItem(new RowId(itemId)).getItemProperty(PostcodeSchema.CODE).getValue().toString();
		postcodeCon.removeAllContainerFilters();
		
		return code;
	}
}

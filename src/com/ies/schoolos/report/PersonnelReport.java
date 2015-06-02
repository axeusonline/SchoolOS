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

import com.ies.schoolos.component.ui.SchoolOSLayout;
import com.ies.schoolos.container.Container;
import com.ies.schoolos.container.DbConnection;
import com.ies.schoolos.schema.CitySchema;
import com.ies.schoolos.schema.DistrictSchema;
import com.ies.schoolos.schema.PostcodeSchema;
import com.ies.schoolos.schema.ProvinceSchema;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.info.FamilySchema;
import com.ies.schoolos.schema.info.PersonnelGraduatedHistorySchema;
import com.ies.schoolos.schema.info.PersonnelSchema;
import com.ies.schoolos.type.AliveStatus;
import com.ies.schoolos.type.BankAccountType;
import com.ies.schoolos.type.Blood;
import com.ies.schoolos.type.EmploymentType;
import com.ies.schoolos.type.Gender;
import com.ies.schoolos.type.GraduatedLevel;
import com.ies.schoolos.type.LicenseLecturerType;
import com.ies.schoolos.type.MaritalStatus;
import com.ies.schoolos.type.Nationality;
import com.ies.schoolos.type.Occupation;
import com.ies.schoolos.type.PeopleIdType;
import com.ies.schoolos.type.Prename;
import com.ies.schoolos.type.Race;
import com.ies.schoolos.type.Religion;
import com.ies.schoolos.type.ResignType;
import com.ies.schoolos.type.dynamic.Department;
import com.ies.schoolos.type.dynamic.JobPosition;
import com.ies.schoolos.utility.DateTimeUtil;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;

public class PersonnelReport {
	
	private final String DEFAULT_FORM = "https://storage.googleapis.com/schoolos/forms/";
	private final String DEFAULT_IMAGE = "https://storage.googleapis.com/schoolos/images/";
	private final String REPORT_ID = "personnel_info.jasper";
	private final String LOGO = SessionSchema.getSchoolID() +".jpg";

	private SQLContainer provinceCon = SchoolOSLayout.container.getProvinceContainer();
	private SQLContainer districtCon = SchoolOSLayout.container.getDistrictContainer();
	private SQLContainer cityCon = SchoolOSLayout.container.getCityContainer();
	private SQLContainer postcodeCon = SchoolOSLayout.container.getPostcodeContainer();
	private SQLContainer familyContainer = SchoolOSLayout.container.getRecruitFamilyContainer();
	
	private StreamResource resource;
	
	public PersonnelReport(int studentId) {
		printReport(studentId);
	}
	
	@SuppressWarnings("deprecation")
	public void printReport(int studentId){
		try {
			final Connection con = DbConnection.getConnection().reserveConnection();
			final Map<String, Object> paramMap = new HashMap<String, Object>();
			Item studentItem = SchoolOSLayout.container.getPersonnelContainer().getItem(new RowId(studentId));
			
			contertoMap(paramMap, studentItem);
			
			StreamResource.StreamSource source = new StreamResource.StreamSource() {
				private static final long serialVersionUID = 1L;

				public InputStream getStream() {
						byte[] b = null;
						try {
			            InputStream rep = new URL(DEFAULT_FORM + REPORT_ID).openStream();
			            //InputStream rep = new FileInputStream(new File("C:\\Users\\Sharif\\Desktop\\Report\\personnel_info.jasper"));
			            
			            getImage(paramMap);
			    		
			            if (rep!=null) {
			              JasperReport report = (JasperReport) JRLoader.loadObject(rep);
			              report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
			              b = JasperRunManager.runReportToPdf(report, paramMap, con);
			            }
			          } catch (Exception ex) {
			            Logger.getLogger(PersonnelReport.class.getName()).log(Level.SEVERE, null, ex);
		
			          }
		          return new ByteArrayInputStream(b);  
				}
			};
			
		    resource = new StreamResource(source, studentItem.getItemProperty(PersonnelSchema.PEOPLE_ID) + ".pdf");
		    Page.getCurrent().open(resource, "_blank",false);
		    
	        DbConnection.getConnection().releaseConnection(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		paramMap.put("school_name",SessionSchema.getSchoolName());
		paramMap.put(PersonnelSchema.PERSONNEL_ID,item.getItemProperty(PersonnelSchema.PERSONNEL_ID).getValue());
		paramMap.put(PersonnelSchema.SCHOOL_ID,item.getItemProperty(PersonnelSchema.SCHOOL_ID).getValue());
		paramMap.put(PersonnelSchema.PEOPLE_ID,item.getItemProperty(PersonnelSchema.PEOPLE_ID).getValue());
		paramMap.put(PersonnelSchema.PEOPLE_ID_TYPE,item.getItemProperty(PersonnelSchema.PEOPLE_ID_TYPE).getValue());
		paramMap.put(PersonnelSchema.PERSONNEL_CODE,item.getItemProperty(PersonnelSchema.PERSONNEL_CODE).getValue());
		paramMap.put(PersonnelSchema.PRENAME,Prename.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.PRENAME).getValue().toString())));
		paramMap.put(PersonnelSchema.FIRSTNAME,item.getItemProperty(PersonnelSchema.FIRSTNAME).getValue());
		paramMap.put(PersonnelSchema.LASTNAME,item.getItemProperty(PersonnelSchema.LASTNAME).getValue());
		paramMap.put(PersonnelSchema.FIRSTNAME_ND,item.getItemProperty(PersonnelSchema.FIRSTNAME_ND).getValue());
		paramMap.put(PersonnelSchema.LASTNAME_ND,item.getItemProperty(PersonnelSchema.LASTNAME_ND).getValue());
		paramMap.put(PersonnelSchema.FIRSTNAME_RD,item.getItemProperty(PersonnelSchema.FIRSTNAME_RD).getValue());
		paramMap.put(PersonnelSchema.LASTNAME_RD,item.getItemProperty(PersonnelSchema.LASTNAME_RD).getValue());
		paramMap.put(PersonnelSchema.NICKNAME,item.getItemProperty(PersonnelSchema.NICKNAME).getValue());
		paramMap.put(PersonnelSchema.GENDER,Gender.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.GENDER).getValue().toString())));
		paramMap.put(PersonnelSchema.RELIGION,Religion.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.RELIGION).getValue().toString())));
		paramMap.put(PersonnelSchema.RACE,Race.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.RACE).getValue().toString())));
		paramMap.put(PersonnelSchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.NATIONALITY).getValue().toString())));
		paramMap.put(PersonnelSchema.MARITAL_STATUS,MaritalStatus.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.MARITAL_STATUS).getValue().toString())));
		paramMap.put(PersonnelSchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.BIRTH_DATE).getValue().toString()));
		paramMap.put(PersonnelSchema.BLOOD,Blood.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.BLOOD).getValue().toString())));
		paramMap.put(PersonnelSchema.HEIGHT,item.getItemProperty(PersonnelSchema.HEIGHT).getValue());
		paramMap.put(PersonnelSchema.WEIGHT,item.getItemProperty(PersonnelSchema.WEIGHT).getValue());
		paramMap.put(PersonnelSchema.CONGENITAL_DISEASE,item.getItemProperty(PersonnelSchema.CONGENITAL_DISEASE).getValue());
		paramMap.put(PersonnelSchema.PERSONNEL_STATUS,item.getItemProperty(PersonnelSchema.PERSONNEL_STATUS).getValue());
		paramMap.put(PersonnelSchema.JOB_POSITION_ID, new JobPosition().getItem(item.getItemProperty(PersonnelSchema.JOB_POSITION_ID).getValue()).getItemProperty("name").getValue().toString());
		paramMap.put(PersonnelSchema.DEPARTMENT_ID,new Department().getItem(item.getItemProperty(PersonnelSchema.DEPARTMENT_ID).getValue()).getItemProperty("name").getValue().toString());
		paramMap.put(PersonnelSchema.LICENSE_LECTURER_NUMBER,item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_NUMBER).getValue());
		if(item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_TYPE).getValue() != null)
			paramMap.put(PersonnelSchema.LICENSE_LECTURER_TYPE,LicenseLecturerType.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_TYPE).getValue().toString())));
		if(item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_ISSUED_DATE).getValue() != null)			
			paramMap.put(PersonnelSchema.LICENSE_LECTURER_ISSUED_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_ISSUED_DATE).getValue().toString()));
		if(item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_EXPIRED_DATE).getValue() != null)
			paramMap.put(PersonnelSchema.LICENSE_LECTURER_EXPIRED_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.LICENSE_LECTURER_EXPIRED_DATE).getValue().toString()));
		paramMap.put(PersonnelSchema.LICENSE_11_NUMBER,item.getItemProperty(PersonnelSchema.LICENSE_11_NUMBER).getValue());
		paramMap.put(PersonnelSchema.LICENSE_ISSUE_AREA,item.getItemProperty(PersonnelSchema.LICENSE_ISSUE_AREA).getValue());
		if(item.getItemProperty(PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID).getValue() != null)
			paramMap.put(PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID,getProvinceName(Integer.parseInt(item.getItemProperty(PersonnelSchema.LICENSE_ISSUE_PROVINCE_ID).getValue().toString())));
		paramMap.put(PersonnelSchema.LICENSE_17_NUMBER,item.getItemProperty(PersonnelSchema.LICENSE_17_NUMBER).getValue());
		paramMap.put(PersonnelSchema.LICENSE_18_NUMBER,item.getItemProperty(PersonnelSchema.LICENSE_18_NUMBER).getValue());
		paramMap.put(PersonnelSchema.LICENSE_19_NUMBER,item.getItemProperty(PersonnelSchema.LICENSE_19_NUMBER).getValue());
		paramMap.put(PersonnelSchema.FILL_DEGREE_POST,item.getItemProperty(PersonnelSchema.FILL_DEGREE_POST).getValue());
		if(item.getItemProperty(PersonnelSchema.FILL_DEGREE_POST_DATE).getValue() != null)
			paramMap.put(PersonnelSchema.FILL_DEGREE_POST_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.FILL_DEGREE_POST_DATE).getValue().toString()));
		paramMap.put(PersonnelSchema.TEL,item.getItemProperty(PersonnelSchema.TEL).getValue());
		paramMap.put(PersonnelSchema.MOBILE,item.getItemProperty(PersonnelSchema.MOBILE).getValue());
		paramMap.put(PersonnelSchema.EMAIL,item.getItemProperty(PersonnelSchema.EMAIL).getValue());
		
		paramMap.put(PersonnelSchema.CENSUS_ADDRESS,item.getItemProperty(PersonnelSchema.CENSUS_ADDRESS).getValue());
		if(item.getItemProperty(PersonnelSchema.CENSUS_CITY_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CENSUS_CITY_ID,getCityName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CENSUS_CITY_ID).getValue().toString())));

		if(item.getItemProperty(PersonnelSchema.CENSUS_DISTRICT_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CENSUS_DISTRICT_ID,getDistrictName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CENSUS_DISTRICT_ID).getValue().toString())));
		if(item.getItemProperty(PersonnelSchema.CENSUS_PROVINCE_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CENSUS_PROVINCE_ID,getProvinceName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CENSUS_PROVINCE_ID).getValue().toString())));
		if(item.getItemProperty(PersonnelSchema.CENSUS_POSTCODE_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CENSUS_POSTCODE_ID,getPostcodeName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CENSUS_POSTCODE_ID).getValue().toString())));
		
		paramMap.put(PersonnelSchema.CURRENT_ADDRESS,item.getItemProperty(PersonnelSchema.CURRENT_ADDRESS).getValue());
		if(item.getItemProperty(PersonnelSchema.CURRENT_CITY_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CURRENT_CITY_ID).getValue().toString())));
		if(item.getItemProperty(PersonnelSchema.CURRENT_DISTRICT_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CURRENT_DISTRICT_ID).getValue().toString())));
		if(item.getItemProperty(PersonnelSchema.CURRENT_PROVINCE_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CURRENT_PROVINCE_ID).getValue().toString())));
		if(item.getItemProperty(PersonnelSchema.CURRENT_POSTCODE_ID).getValue() != null)
			paramMap.put(PersonnelSchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(item.getItemProperty(PersonnelSchema.CURRENT_POSTCODE_ID).getValue().toString())));
		
		paramMap.put(PersonnelSchema.EMPLOYMENT_TYPE,EmploymentType.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.EMPLOYMENT_TYPE).getValue().toString())));
		paramMap.put(PersonnelSchema.START_WORK_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.START_WORK_DATE).getValue().toString()));
		paramMap.put(PersonnelSchema.RECRUIT_BY_ID,item.getItemProperty(PersonnelSchema.RECRUIT_BY_ID).getValue());
		if(item.getItemProperty(PersonnelSchema.RECRUIT_DATE).getValue() != null)
			paramMap.put(PersonnelSchema.RECRUIT_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.RECRUIT_DATE).getValue().toString()));
		paramMap.put(PersonnelSchema.RESIGN_BY_ID,item.getItemProperty(PersonnelSchema.RESIGN_BY_ID).getValue());
		if(item.getItemProperty(PersonnelSchema.RESIGN_DATE).getValue() != null)
			paramMap.put(PersonnelSchema.RESIGN_DATE,DateTimeUtil.getDDMMYYYYBD(item.getItemProperty(PersonnelSchema.RESIGN_DATE).getValue().toString()));
		if(item.getItemProperty(PersonnelSchema.RESIGN_TYPE).getValue() != null)
			paramMap.put(PersonnelSchema.RESIGN_TYPE,ResignType.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.RESIGN_TYPE).getValue().toString())));
		paramMap.put(PersonnelSchema.RESIGN_DESCRIPTION,item.getItemProperty(PersonnelSchema.RESIGN_DESCRIPTION).getValue());
		paramMap.put(PersonnelSchema.BANK_NAME,item.getItemProperty(PersonnelSchema.BANK_NAME).getValue());
		paramMap.put(PersonnelSchema.BANK_ACCOUNT_NUMBER,item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_NUMBER).getValue());
		if(item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_TYPE).getValue() != null)
			paramMap.put(PersonnelSchema.BANK_ACCOUNT_TYPE,BankAccountType.getNameTh(Integer.parseInt(item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_TYPE).getValue().toString())));
		paramMap.put(PersonnelSchema.BANK_ACCOUNT_NAME,item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_NAME).getValue());
		paramMap.put(PersonnelSchema.BANK_ACCOUNT_BRANCH,item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_BRANCH).getValue());
		if(item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID).getValue() != null)
			paramMap.put(PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID,getProvinceName(Integer.parseInt(item.getItemProperty(PersonnelSchema.BANK_ACCOUNT_PROVINCE_ID).getValue().toString())));	

		StringBuilder graduatedBuilder = new StringBuilder();
		graduatedBuilder.append(" SELECT * FROM " + PersonnelGraduatedHistorySchema.TABLE_NAME);
		graduatedBuilder.append(" WHERE " + PersonnelGraduatedHistorySchema.PERSONNEL_ID + "=" + item.getItemProperty(PersonnelSchema.PERSONNEL_ID).getValue());
		graduatedBuilder.append(" ORDER BY " + PersonnelGraduatedHistorySchema.YEAR);
		graduatedBuilder.append(" LIMIT 1");
		
		SQLContainer freeContainer = Container.getFreeFormContainer(graduatedBuilder.toString(), PersonnelGraduatedHistorySchema.GRADUATED_HISTORY_ID);
		if(freeContainer.size() > 1){
			Item graduatedItem = freeContainer.getItem(freeContainer.getIdByIndex(0));
			paramMap.put(PersonnelGraduatedHistorySchema.INSTITUTE,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.INSTITUTE).getValue());
			paramMap.put(PersonnelGraduatedHistorySchema.GRADUATED_LEVEL,GraduatedLevel.getNameTh(Integer.parseInt(graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.GRADUATED_LEVEL).getValue().toString())));
			paramMap.put(PersonnelGraduatedHistorySchema.DEGREE,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.DEGREE).getValue());
			paramMap.put(PersonnelGraduatedHistorySchema.MAJOR,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.MAJOR).getValue());
			paramMap.put(PersonnelGraduatedHistorySchema.MINOR,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.MINOR).getValue());
			paramMap.put(PersonnelGraduatedHistorySchema.DESCRIPTION,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.DESCRIPTION).getValue());
			paramMap.put(PersonnelGraduatedHistorySchema.YEAR,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.YEAR).getValue());
			paramMap.put(PersonnelGraduatedHistorySchema.LOCATION,graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.LOCATION).getValue());
			if(item.getItemProperty(PersonnelGraduatedHistorySchema.PROVINCE_ID).getValue() != null)
				paramMap.put(PersonnelGraduatedHistorySchema.PROVINCE_ID,getProvinceName(Integer.parseInt(graduatedItem.getItemProperty(PersonnelGraduatedHistorySchema.PROVINCE_ID).getValue().toString())));
		}
		
		
		/* FATHER */
		Object fatherId = item.getItemProperty(PersonnelSchema.FATHER_ID).getValue();
		if(fatherId != null){
			Item fatherItem = familyContainer.getItem(new RowId(fatherId));
			paramMap.put(PersonnelSchema.FATHER_ID,item.getItemProperty(PersonnelSchema.FATHER_ID).getValue());
			paramMap.put("f_" + FamilySchema.FAMILY_ID,fatherItem.getItemProperty(FamilySchema.FAMILY_ID).getValue());
			paramMap.put("f_" + FamilySchema.PEOPLE_ID,fatherItem.getItemProperty(FamilySchema.PEOPLE_ID).getValue());
			paramMap.put("f_" + FamilySchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.PEOPLE_ID_TYPE).getValue().toString())));
			paramMap.put("f_" + FamilySchema.PRENAME,Prename.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.PRENAME).getValue().toString())));
			paramMap.put("f_" + FamilySchema.FIRSTNAME,fatherItem.getItemProperty(FamilySchema.FIRSTNAME).getValue());
			paramMap.put("f_" + FamilySchema.LASTNAME,fatherItem.getItemProperty(FamilySchema.LASTNAME).getValue());
			paramMap.put("f_" + FamilySchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.PRENAME).getValue().toString())));
			paramMap.put("f_" + FamilySchema.FIRSTNAME_ND,fatherItem.getItemProperty(FamilySchema.FIRSTNAME_ND).getValue());
			paramMap.put("f_" + FamilySchema.LASTNAME_ND,fatherItem.getItemProperty(FamilySchema.LASTNAME_ND).getValue());
			paramMap.put("f_" + FamilySchema.GENDER,Gender.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.GENDER).getValue().toString())));
			paramMap.put("f_" + FamilySchema.RELIGION,Religion.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.RELIGION).getValue().toString())));
			paramMap.put("f_" + FamilySchema.RACE,Race.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.RACE).getValue().toString())));
			paramMap.put("f_" + FamilySchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.NATIONALITY).getValue().toString())));
			if(fatherItem.getItemProperty(FamilySchema.BIRTH_DATE).getValue() != null)
				paramMap.put("f_" + FamilySchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(fatherItem.getItemProperty(FamilySchema.BIRTH_DATE).getValue().toString()));
			paramMap.put("f_" + FamilySchema.TEL,fatherItem.getItemProperty(FamilySchema.TEL).getValue());
			paramMap.put("f_" + FamilySchema.MOBILE,fatherItem.getItemProperty(FamilySchema.MOBILE).getValue());
			paramMap.put("f_" + FamilySchema.EMAIL,fatherItem.getItemProperty(FamilySchema.EMAIL).getValue());
			paramMap.put("f_" + FamilySchema.SALARY,fatherItem.getItemProperty(FamilySchema.SALARY).getValue());
			paramMap.put("f_" + FamilySchema.ALIVE_STATUS,AliveStatus.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.ALIVE_STATUS).getValue().toString())));
			paramMap.put("f_" + FamilySchema.OCCUPATION,Occupation.getNameTh(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.OCCUPATION).getValue().toString())));
			paramMap.put("f_" + FamilySchema.JOB_ADDRESS,fatherItem.getItemProperty(FamilySchema.JOB_ADDRESS).getValue());
			paramMap.put("f_" + FamilySchema.CURRENT_ADDRESS,fatherItem.getItemProperty(FamilySchema.CURRENT_ADDRESS).getValue());		
			paramMap.put("f_" + FamilySchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.CURRENT_CITY_ID).getValue().toString())));
			paramMap.put("f_" + FamilySchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.CURRENT_DISTRICT_ID).getValue().toString())));
			paramMap.put("f_" + FamilySchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.CURRENT_PROVINCE_ID).getValue().toString())));
			paramMap.put("f_" + FamilySchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(fatherItem.getItemProperty(FamilySchema.CURRENT_POSTCODE_ID).getValue().toString())));		
		}
		
		/* Mother */
		Object motherId = item.getItemProperty(PersonnelSchema.MOTHER_ID).getValue();
		if(motherId != null){
			Item motherItem = familyContainer.getItem(new RowId(motherId));
			paramMap.put(PersonnelSchema.MOTHER_ID,item.getItemProperty(PersonnelSchema.MOTHER_ID).getValue());
			paramMap.put("m_" + FamilySchema.FAMILY_ID,motherItem.getItemProperty(FamilySchema.FAMILY_ID).getValue());
			paramMap.put("m_" + FamilySchema.PEOPLE_ID,motherItem.getItemProperty(FamilySchema.PEOPLE_ID).getValue());
			paramMap.put("m_" + FamilySchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.PEOPLE_ID_TYPE).getValue().toString())));
			paramMap.put("m_" + FamilySchema.PRENAME,Prename.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.PRENAME).getValue().toString())));
			paramMap.put("m_" + FamilySchema.FIRSTNAME,motherItem.getItemProperty(FamilySchema.FIRSTNAME).getValue());
			paramMap.put("m_" + FamilySchema.LASTNAME,motherItem.getItemProperty(FamilySchema.LASTNAME).getValue());
			paramMap.put("m_" + FamilySchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(motherItem.getItemProperty(FamilySchema.PRENAME).getValue().toString())));
			paramMap.put("m_" + FamilySchema.FIRSTNAME_ND,motherItem.getItemProperty(FamilySchema.FIRSTNAME_ND).getValue());
			paramMap.put("m_" + FamilySchema.LASTNAME_ND,motherItem.getItemProperty(FamilySchema.LASTNAME_ND).getValue());
			paramMap.put("m_" + FamilySchema.GENDER,Gender.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.GENDER).getValue().toString())));
			paramMap.put("m_" + FamilySchema.RELIGION,Religion.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.RELIGION).getValue().toString())));
			paramMap.put("m_" + FamilySchema.RACE,Race.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.RACE).getValue().toString())));
			paramMap.put("m_" + FamilySchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.NATIONALITY).getValue().toString())));
			if(motherItem.getItemProperty(FamilySchema.BIRTH_DATE).getValue() != null)
				paramMap.put("m_" + FamilySchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(motherItem.getItemProperty(FamilySchema.BIRTH_DATE).getValue().toString()));
			paramMap.put("m_" + FamilySchema.TEL,motherItem.getItemProperty(FamilySchema.TEL).getValue());
			paramMap.put("m_" + FamilySchema.MOBILE,motherItem.getItemProperty(FamilySchema.MOBILE).getValue());
			paramMap.put("m_" + FamilySchema.EMAIL,motherItem.getItemProperty(FamilySchema.EMAIL).getValue());
			paramMap.put("m_" + FamilySchema.SALARY,motherItem.getItemProperty(FamilySchema.SALARY).getValue());
			paramMap.put("m_" + FamilySchema.ALIVE_STATUS,AliveStatus.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.ALIVE_STATUS).getValue().toString())));
			paramMap.put("m_" + FamilySchema.OCCUPATION,Occupation.getNameTh(Integer.parseInt(motherItem.getItemProperty(FamilySchema.OCCUPATION).getValue().toString())));
			paramMap.put("m_" + FamilySchema.JOB_ADDRESS,motherItem.getItemProperty(FamilySchema.JOB_ADDRESS).getValue());
			paramMap.put("m_" + FamilySchema.CURRENT_ADDRESS,motherItem.getItemProperty(FamilySchema.CURRENT_ADDRESS).getValue());		
			paramMap.put("m_" + FamilySchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(motherItem.getItemProperty(FamilySchema.CURRENT_CITY_ID).getValue().toString())));
			paramMap.put("m_" + FamilySchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(motherItem.getItemProperty(FamilySchema.CURRENT_DISTRICT_ID).getValue().toString())));
			paramMap.put("m_" + FamilySchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(motherItem.getItemProperty(FamilySchema.CURRENT_PROVINCE_ID).getValue().toString())));
			paramMap.put("m_" + FamilySchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(motherItem.getItemProperty(FamilySchema.CURRENT_POSTCODE_ID).getValue().toString())));					
		}
		
		/* Spouse */
		Object spouseId = item.getItemProperty(PersonnelSchema.SPOUSE_ID).getValue();
		if(spouseId != null){
			Item spouseItem = familyContainer.getItem(new RowId(spouseId));
			paramMap.put(PersonnelSchema.SPOUSE_ID,item.getItemProperty(PersonnelSchema.SPOUSE_ID).getValue());
			paramMap.put("s_" + FamilySchema.FAMILY_ID,spouseItem.getItemProperty(FamilySchema.FAMILY_ID).getValue());
			paramMap.put("s_" + FamilySchema.PEOPLE_ID,spouseItem.getItemProperty(FamilySchema.PEOPLE_ID).getValue());
			paramMap.put("s_" + FamilySchema.PEOPLE_ID_TYPE,PeopleIdType.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.PEOPLE_ID_TYPE).getValue().toString())));
			paramMap.put("s_" + FamilySchema.PRENAME,Prename.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.PRENAME).getValue().toString())));
			paramMap.put("s_" + FamilySchema.FIRSTNAME,spouseItem.getItemProperty(FamilySchema.FIRSTNAME).getValue());
			paramMap.put("s_" + FamilySchema.LASTNAME,spouseItem.getItemProperty(FamilySchema.LASTNAME).getValue());
			paramMap.put("s_" + FamilySchema.PRENAME_ND,Prename.getNameEn(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.PRENAME).getValue().toString())));
			paramMap.put("s_" + FamilySchema.FIRSTNAME_ND,spouseItem.getItemProperty(FamilySchema.FIRSTNAME_ND).getValue());
			paramMap.put("s_" + FamilySchema.LASTNAME_ND,spouseItem.getItemProperty(FamilySchema.LASTNAME_ND).getValue());
			paramMap.put("s_" + FamilySchema.GENDER,Gender.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.GENDER).getValue().toString())));
			paramMap.put("s_" + FamilySchema.RELIGION,Religion.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.RELIGION).getValue().toString())));
			paramMap.put("s_" + FamilySchema.RACE,Race.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.RACE).getValue().toString())));
			paramMap.put("s_" + FamilySchema.NATIONALITY,Nationality.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.NATIONALITY).getValue().toString())));
			if(spouseItem.getItemProperty(FamilySchema.BIRTH_DATE).getValue() != null)
				paramMap.put("s_" + FamilySchema.BIRTH_DATE,DateTimeUtil.getDDMMYYYYBD(spouseItem.getItemProperty(FamilySchema.BIRTH_DATE).getValue().toString()));
			paramMap.put("s_" + FamilySchema.TEL,spouseItem.getItemProperty(FamilySchema.TEL).getValue());
			paramMap.put("s_" + FamilySchema.MOBILE,spouseItem.getItemProperty(FamilySchema.MOBILE).getValue());
			paramMap.put("s_" + FamilySchema.EMAIL,spouseItem.getItemProperty(FamilySchema.EMAIL).getValue());
			paramMap.put("s_" + FamilySchema.SALARY,spouseItem.getItemProperty(FamilySchema.SALARY).getValue());
			paramMap.put("s_" + FamilySchema.ALIVE_STATUS,AliveStatus.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.ALIVE_STATUS).getValue().toString())));
			paramMap.put("s_" + FamilySchema.OCCUPATION,Occupation.getNameTh(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.OCCUPATION).getValue().toString())));
			paramMap.put("s_" + FamilySchema.JOB_ADDRESS,spouseItem.getItemProperty(FamilySchema.JOB_ADDRESS).getValue());
			paramMap.put("s_" + FamilySchema.CURRENT_ADDRESS,spouseItem.getItemProperty(FamilySchema.CURRENT_ADDRESS).getValue());		
			paramMap.put("s_" + FamilySchema.CURRENT_CITY_ID,getCityName(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.CURRENT_CITY_ID).getValue().toString())));
			paramMap.put("s_" + FamilySchema.CURRENT_DISTRICT_ID,getDistrictName(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.CURRENT_DISTRICT_ID).getValue().toString())));
			paramMap.put("s_" + FamilySchema.CURRENT_PROVINCE_ID,getProvinceName(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.CURRENT_PROVINCE_ID).getValue().toString())));
			paramMap.put("s_" + FamilySchema.CURRENT_POSTCODE_ID,getPostcodeName(Integer.parseInt(spouseItem.getItemProperty(FamilySchema.CURRENT_POSTCODE_ID).getValue().toString())));	
		}
	}

	private String getProvinceName(int itemId){
		provinceCon.addContainerFilter(new Equal(ProvinceSchema.PROVINCE_ID,itemId));
		String name = provinceCon.getItem(new RowId(itemId)).getItemProperty(ProvinceSchema.NAME).getValue().toString();
		provinceCon.removeAllContainerFilters();
		
		return name;
	}

	private String getDistrictName(int itemId){
		districtCon.addContainerFilter(new Equal(DistrictSchema.DISTRICT_ID,itemId));
		String name = districtCon.getItem(new RowId(itemId)).getItemProperty(DistrictSchema.NAME).getValue().toString();
		districtCon.removeAllContainerFilters();
		
		return  name;
	}

	private String getCityName(int itemId){
		cityCon.addContainerFilter(new Equal(CitySchema.CITY_ID,itemId));
		String name = cityCon.getItem(new RowId(itemId)).getItemProperty(CitySchema.NAME).getValue().toString();
		cityCon.removeAllContainerFilters();
		
		return name;
	}

	private String getPostcodeName(int itemId){
		postcodeCon.addContainerFilter(new Equal(PostcodeSchema.POSTCODE_ID,itemId));
		String code = postcodeCon.getItem(new RowId(itemId)).getItemProperty(PostcodeSchema.CODE).getValue().toString();
		postcodeCon.removeAllContainerFilters();
		
		return code;
	}
}

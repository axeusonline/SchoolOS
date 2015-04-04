package com.ies.schoolos.component.statistic;

import java.util.HashMap;

import org.vaadin.highcharts.HighChart;

import com.ies.schoolos.schema.view.StatRecruitStudentQty;
import com.ies.schoolos.service.FreeFormContainerService;
import com.ies.schoolos.type.ClassRange;
import com.vaadin.data.Item;
import com.vaadin.ui.GridLayout;

public class RecruitStudentStat extends GridLayout{
	private static final long serialVersionUID = 1L;

	private String series;
	
	private FreeFormContainerService statContainer = new FreeFormContainerService("SELECT * FROM " + StatRecruitStudentQty.TABLE_NAME, StatRecruitStudentQty.STUDENT_QTY);
	private HashMap<String, Integer> data = new HashMap<String, Integer>();
	
	private HighChart classRangeChart;
	/*private Chart genderChart;
	private Chart provinceChart;
	private Chart bloodChart;*/

	public RecruitStudentStat() {
		buildMainLayout();
	}
	
	private void buildMainLayout(){
		setSizeFull();
		setColumns(2);
		setRows(2);
		
		initClassRangeChart();
	/*	initGenderChart();
		initProvinceChart();*/
	}
	
	private void initClassRangeChart(){
		classRangeChart = new HighChart();
		addComponent(classRangeChart);
		
		
		/*Configuration conf = classRangeChart.getConfiguration();
		 
		conf.setTitle("นักเรียนทั้ง, ช่วงชั้น");
		conf.setSubTitle("สร้างสรรค์โดย www.schoolosplus.com");
		
		XAxis x = new XAxis();
        x.setCategories("2558");
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("จำนวนผู้สมัคร (คน)");
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor("#FFFFFF");
        legend.setHorizontalAlign(HorizontalAlign.LEFT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(100);
        legend.setY(70);
        legend.setFloating(true);
        legend.setShadow(true);
        conf.setLegend(legend);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.x +': '+ this.y +' mm'");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);

        conf.addSeries(new ListSeries("Tokyo", 49.9, 71.5, 106.4, 129.2, 144.0,
                176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4));
        conf.addSeries(new ListSeries("New York", 83.6, 78.8, 98.5, 93.4,
                106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3));
        conf.addSeries(new ListSeries("London", 48.9, 38.8, 39.3, 41.4, 47.0,
                48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2));
        conf.addSeries(new ListSeries("Berlin", 42.4, 33.2, 34.5, 39.7, 52.6,
                75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1));*/
        //Add data to HashMap
     /*   for(Object itemId:statContainer.getContainer().getItemIds()){
        	Item item = statContainer.getContainer().getItem(itemId);
        	int recStudentQty = 1;
        	
        	if(data.get(item.getItemProperty(StatRecruitStudentQty.CLASS_RANGE).getValue().toString()) != null){
        		recStudentQty = data.get(item.getItemProperty(StatRecruitStudentQty.CLASS_RANGE).getValue().toString());
        		recStudentQty++;
        	}
        	
        	data.put(item.getItemProperty(StatRecruitStudentQty.CLASS_RANGE).getValue().toString(), recStudentQty);
        }
        
        //Add data to Chart
        for(int i = 0; i < new ClassRange().size(); i++){
        	if(data.get(Integer.toString(i)) != null){
        		conf.addSeries(new ListSeries(ClassRange.getNameTh(i), data.get(Integer.toString(i))));
        	}
        }

        classRangeChart.setHcjs("var options = { title: { text: 'test diagram' }, chart: {type: 'column'}, series: [{ name: 's1', data: [1, 3, 2]}] };");*/
	}
	
	/*private void initGenderChart(){
			
	}
	
	private void initProvinceChart(){
		
	}
	
	private void initBloodChart(){
		
	}*/
}

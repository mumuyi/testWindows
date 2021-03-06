package com.nuaa.ai;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class DataWindow {

	DateFormat fmt = DateFormat.getDateTimeInstance();

	//绘制饼状图;
	private void DrawMyPireChar() {

		DefaultPieDataset dpd = new DefaultPieDataset(); // 建立一个默认的饼图
		dpd.setValue("test1", 25); // 输入数据 dpd.setValue("test2", 25);
		dpd.setValue("test3", 30);
		dpd.setValue("test4", 10);

		JFreeChart chart = ChartFactory.createPieChart("data show", dpd, true, true, false); //
		// 可以查具体的API文档,第一个参数是标题，第二个参数是一个数据集，第三个参数表示是否显示Legend，第四个参数表示是否显示提示，
		// 第五个参数表示图中是否存在URL

		ChartFrame chartFrame = new ChartFrame("data show", chart); //
		// chart要放在Java容器组件中，ChartFrame继承自java的Jframe类。该第一个参数的数据是放在窗口左上角的，
		// 不是正中间的标题。
		chartFrame.pack(); // 以合适的大小展现图形
		RefineryUtilities.centerFrameOnScreen(chartFrame);
		chartFrame.setVisible(true);// 图形是否可见

	}

	public JFreeChart getPireChar(){
		DefaultPieDataset dpd = new DefaultPieDataset(); // 建立一个默认的饼图
		dpd.setValue("test1", 25); // 输入数据 dpd.setValue("test2", 25);
		dpd.setValue("test3", 30);
		dpd.setValue("test4", 10);

		return ChartFactory.createPieChart("data show", dpd, true, true, false); 
	}
	
	
	public void showPireChar(){
		//showd.createChart();
		DrawMyPireChar();
		createChart();
	}
	
	
	
	// 获得数据集 （这里的数据是为了测试我随便写的一个自动生成数据的例子）
	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
		// 曲线名称
		String series = " glucose"; // series指的就是报表里的那条数据线
		// 因此 对数据线的相关设置就需要联系到serise
		// 比如说setSeriesPaint 设置数据线的颜色
		// 横轴名称(列名称)
		String[] time = new String[15];
		String[] timeValue = { "6-1", "6-2", "6-3", "6-4", "6-5", "6-6", "6-7", "6-8", "6-9", "6-10", "6-11", "6-12",
				"6-13", "6-14", "6-15" };

		for (int i = 0; i < 15; i++) {
			time[i] = timeValue[i];
		}
		// 随机添加数据值
		for (int i = 0; i < 15; i++) {
			java.util.Random r = new java.util.Random();
			linedataset.addValue(i + i * 9.34 + r.nextLong() % 100, // 值
					series, // 哪条数据线
					time[i]); // 对应的横轴
		}
		return linedataset;
	}

	// 绘制静态折线图;
	/*
	 * 整个大的框架属于JFreeChart
	 * 坐标轴里的属于 Plot 其常用子类有：CategoryPlot, MultiplePiePlot, PiePlot , XYPlot
	 */
	private void createChart() {

		// 定义图标对象
		JFreeChart chart = ChartFactory.createLineChart(null, // 报表题目，字符串类型
				"Time", // 横轴
				"glucose", // 纵轴
				this.createDataset(), // 获得数据集
				PlotOrientation.VERTICAL, // 图标方向垂直
				true, // 显示图例
				false, // 不用生成工具
				false // 不用生成URL地址
		);

		// 整个大的框架属于chart 可以设置chart的背景颜色
		// 生成图形
		CategoryPlot plot = chart.getCategoryPlot();
		// 图像属性部分
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinesVisible(true); // 设置背景网格线是否可见
		plot.setDomainGridlinePaint(Color.BLACK); // 设置背景网格线颜色
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setNoDataMessage("no data");// 没有数据时显示的文字说明

		// 数据轴属性部分
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true); // 自动生成
		rangeAxis.setUpperMargin(0.20);
		rangeAxis.setLabelAngle(Math.PI / 2.0);
		rangeAxis.setAutoRange(false);

		// 数据渲染部分 主要是对折线做操作
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseItemLabelsVisible(true);
		renderer.setSeriesPaint(0, Color.black); // 设置折线的颜色
		renderer.setBaseShapesFilled(true);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBasePositiveItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

		/*
		 * 
		 * 这里的StandardCategoryItemLabelGenerator()我想强调下：当时这个地方被搅得头很晕，
		 * Standard**ItemLabelGenerator是通用的 因为我创建的是CategoryPlot
		 * 所以很多设置都是Category相关
		 * 
		 * 而XYPlot 对应的则是 ： StandardXYItemLabelGenerator
		 * 
		 */

		renderer.setBaseItemLabelFont(new Font("Dialog", 1, 14)); // 设置提示折点数据形状
		plot.setRenderer(renderer);

		// 区域渲染部分
		double lowpress = 4.5;
		double uperpress = 20; // 设定正常血糖值的范围
		IntervalMarker inter = new IntervalMarker(lowpress, uperpress);
		inter.setLabelOffsetType(LengthAdjustmentType.EXPAND); // 范围调整——扩张
		inter.setPaint(Color.LIGHT_GRAY);// 域顏色
		inter.setLabelFont(new Font("SansSerif", 41, 14));
		inter.setLabelPaint(Color.RED);
		inter.setLabel("normal"); // 设定区域说明文字
		plot.addRangeMarker(inter, Layer.BACKGROUND); // 添加mark到图形
														// BACKGROUND使得数据折线在区域的前端

		/*
		 * // 创建文件输出流 File fos_jpg = new File("E://bloodSugarChart.jpg "); //
		 * 输出到哪个输出流 ChartUtilities.saveChartAsJPEG(fos_jpg, chart, // 统计图表对象
		 * 700, // 宽 500 // 高 );
		 */

		ChartFrame chartFrame = new ChartFrame("data show", chart); // chart要放在Java容器组件中，ChartFrame继承自java的Jframe类。该第一个参数的数据是放在窗口左上角的，不是正中间的标题。
		//chartFrame.pack(); // 以合适的大小展现图形
		RefineryUtilities.centerFrameOnScreen(chartFrame);
		chartFrame.setVisible(true);// 图形是否可见
	}
}

package com.nuaa.ai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableWindowsFactory implements ActionListener ,MouseListener{

	private JFrame frame = null;
	private JTable table = null;
	private MyTableModel model = null;
	private JScrollPane s_pan = null;

	// 翻页按钮;
	private JButton nextPagebutton;
	private JButton lastPagebutton;
	// 页数信息;
	private JLabel pageLabel;

	// 数据库查询时,每次查询的最大数量;
	private int maxLineATime = 4;
	// 当前页数;
	private int currentPage = 0;
	// 表中的数据的总的数量;
	private long totalNum = 0;
	// 暂存表中每一列的值;
	private List<String> list;
	// 查询结果;
	private List<?> sqlList;
	// 调用的类;
	private Object object;
	//搜索条件输入框;
	private TextField text;
	//搜索条件;
	private String searchCondition=null;
	// 特殊行数据;
	private List<Integer> specialrow = new ArrayList<>();
	//表头;
	private String[] TableTitles;
	//需要删除的数据;
	private List<Integer> delList=new ArrayList<Integer>();
	// 构造函数;
	/**
	 * TableTitle 窗口名; TableTitles 表头; objectList 表中第一页的数据; formerObject
	 * 实例化本类的那个类,用来回调获取更新的数据; totalNum 表中的数据总数;
	 * 
	 */
	public TableWindowsFactory(String TableTitle, String[] TableTitles, List<?> objectList, Object formerObject,
			long totalNum) {
		frame = new JFrame(TableTitle);
		model = new MyTableModel(TableTitles, 20);
		table = new JTable(model);

		object = formerObject;
		sqlList = objectList;
		this.totalNum = totalNum;
		this.TableTitles=TableTitles;
		
		// 初始化首页数据;
		for (int i = 0; i < sqlList.size(); i++) {
			try {
				list = getObjectValue(sqlList.get(i), i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addRow(list);
		}

		//设置第一列行号的宽度为固定值;
		TableColumn firsetColumn = table.getColumnModel().getColumn(0);
		firsetColumn.setPreferredWidth(30);
		firsetColumn.setMaxWidth(30);
		firsetColumn.setMinWidth(30);

		// 增加测试数据,测试表格中对特殊行显示特殊的颜色;
		// specialrow.add(0);

		// 设置选中的行的颜色;
		table.setSelectionBackground(new Color(189, 252, 201));
		// 表格背景色
		// table.setBackground(Color.yellow);
		// 指定每一行的行高50;
		table.setRowHeight(50);
		// table.setRowHeight(2, 30);//指定2行的高度30

		// 给表格增加颜色;
		makeFace(table, specialrow);
		
		//添加表格单元格点击事件;
		table.addMouseListener(this);

		// 将表格加入窗口中;
		s_pan = new JScrollPane(table);
		frame.add(s_pan);

		//添加按钮和页数信息;
		addBotton();

		//添加搜索框;
		addSearch();
		
		// 设置窗口大小;
		frame.setSize(600, 400);

		// 设置JFrame 窗口关闭事件;
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// 设置JFrame 窗口出现位置;这里设置其显示在屏幕中间;
		frame.setLocationRelativeTo(null);
	}

	// 显示窗口;
	public void showTable() {
		frame.setVisible(true);
	}

	// 为表格上色;
	private void makeFace(JTable table, List<Integer> specialrows) {
		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
				private static final long serialVersionUID = -3876410206046533481L;

				List<Integer> specialrow = specialrows;

				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					//设置cell 中的数据居中显示;
					setHorizontalAlignment((int) 0.5f);
					
					// 设置相邻两行的颜色不同,方便用户查看;
					if (row % 2 == 0) {
						setBackground(Color.white); // 设置奇数行底色
					} else if (row % 2 == 1) {
						setBackground(Color.GRAY); // 设置偶数行底色
					}
					// 如果需要设置某一个Cell颜色，需要加上column过滤条件即可
					 if (searchCondition!=null&&value.toString().contains(searchCondition)&&column!=0) {
						 setBackground(Color.YELLOW); 
					 }
					 
					// 设置特定行的颜色;将所有需要特殊化处理的行号加入specialrow
					// 中,之后再判断当前处理的行号是否包含在specialrow 中即可;
					if (specialrow.contains(row)) {
						setBackground(Color.RED);
					}
					// 这是在干什么不是太清楚,但是感觉应该是有用的;
					/*
					 * if (Double.parseDouble(table.getValueAt(row,
					 * 1).toString()) > 0) { setBackground(Color.red); }
					 */
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			};
			//在这里不做最后一项,在用最开始的方法即可设置成可选的复选框,但是依旧是没有上色的.
			for (int i = 0; i < table.getColumnCount()-1; i++) {
				table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	//添加按钮和页数;
	private void addBotton(){
		// 创建JPanel;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		// 添加上一页按钮;
		lastPagebutton = new JButton("Last Page");
		lastPagebutton.setActionCommand("LastPage");
		lastPagebutton.addActionListener(this);
		buttonPanel.add(lastPagebutton, BorderLayout.WEST);
		// 添加页数信息;
		pageLabel = new JLabel();
		pageLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
		if (totalNum % maxLineATime == 0)
			pageLabel.setText("" + (currentPage + 1) + "/" + (totalNum / maxLineATime));
		else
			pageLabel.setText("" + (currentPage + 1) + "/" + (totalNum / maxLineATime + 1));
		buttonPanel.add(pageLabel, BorderLayout.WEST);
		// 添加下一页按钮;
		nextPagebutton = new JButton("Next Page");
		nextPagebutton.setActionCommand("NextPage");
		nextPagebutton.addActionListener(this);
		buttonPanel.add(nextPagebutton, BorderLayout.EAST);
		// 将JPanel 添加到窗口中;
		frame.add(buttonPanel, BorderLayout.SOUTH);

		// 第一页时设置上一页按钮不可用;
		lastPagebutton.setEnabled(false);
	}
	
	private void addSearch(){
		// 创建JPanel;
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		//添加输入框;
		text = new TextField();
		//text.setBounds(0, 0, 200, 5);
		searchPanel.add(text);
		
		// 添加搜索按钮;
		JButton searchButton = new JButton("Search");
		searchButton.setActionCommand("Search");
		searchButton.addActionListener(this);
		searchPanel.add(searchButton);
		
		// 添加删除按钮;
		JButton delButton = new JButton("Delect");
		delButton.setActionCommand("delect");
		delButton.addActionListener(this);
		searchPanel.add(delButton, BorderLayout.WEST);
		
		// 将JPanel 添加到窗口中;
		frame.add(searchPanel, BorderLayout.NORTH);
	}
	
	
	
	// 鼠标监听事件;
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("NextPage")) {
			System.out.println("在这里向下翻页操作");
			currentPage += 1;
			try {
				updateTable();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("LastPage")) {
			System.out.println("在这里向上翻页操作");
			currentPage -= 1;
			try {
				updateTable();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("Search")) {
			System.out.println("在这里进行搜索");
			searchCondition=text.getText();
			// 给表格增加颜色;
			makeFace(table, specialrow);
			try {
				updateTable();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(e.getActionCommand().equals("delect")){
			System.out.println("在这里进行删除操作");
			
			//删除delList中的对应的sqlList中的对象;
			for(int i=0;i<delList.size();i++){
				MyHibernate.sqlDelete(sqlList.get(delList.get(i)));
				System.out.println(""+delList.get(i));
			}
			
			//更新表格;
			try {
				updateTable();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	// 数据更新操作;
	private void updateTable() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 删除model中的之前的数据;
		for (int i = 0; i < sqlList.size(); i++) {
			// 这里每次都是删除第0个;因为底层的代码在删除之后做了移位了;所以每次都删第一个,一共删除list.size()次就好了;
			model.removeRow(0);
		}
		// 重新查询;
		try {
			Method m = (Method) object.getClass().getMethod("update", int.class, int.class);
			sqlList = (List<?>) m.invoke(object, currentPage * maxLineATime, maxLineATime);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 给model写入新的数据;
		for (int i = 0; i < sqlList.size(); i++) {
			try {
				list = getObjectValue(sqlList.get(i), i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addRow(list);
		}
		// 刷新表格;
		table.updateUI();

		// 更新按钮;
		buttonClickableChange();

		// 更新页数信息;
		if (totalNum % maxLineATime == 0)
			pageLabel.setText("" + (currentPage + 1) + "/" + (totalNum / maxLineATime));
		else
			pageLabel.setText("" + (currentPage + 1) + "/" + (totalNum / maxLineATime + 1));
	}

	// 设置翻页按钮是否可点击;
	private void buttonClickableChange() {
		// 下一页;
		if ((currentPage + 1) * maxLineATime >= totalNum) {
			nextPagebutton.setEnabled(false);
		} else {
			nextPagebutton.setEnabled(true);

		}

		// 上一页;
		if (currentPage == 0) {
			lastPagebutton.setEnabled(false);
		} else {
			lastPagebutton.setEnabled(true);
		}
	}

	// 通过返回获取Object 的具体值;
	private List<String> getObjectValue(Object object, int i) throws Exception {
		List<String> list = new ArrayList<>();
		if (object != null) {

			// 添加编号;
			list.add("" + (i + 1 + currentPage * maxLineATime));

			// 拿到该类
			Class<?> clz = object.getClass();
			// 获取实体类的所有属性，返回Field数组
			Field[] fields = clz.getDeclaredFields();

			for (Field field : fields) {
				// System.out.println(field.getGenericType());// 打印该类的所有属性类型

				// 如果类型是String
				if (field.getGenericType().toString().equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class
																							// "，后面跟类名
					// 拿到该属性的gettet方法
					/**
					 * 这里需要说明一下：他是根据拼凑的字符来找你写的getter方法的
					 * 在Boolean值的时候是isXXX（默认使用ide生成getter的都是isXXX）
					 * 如果出现NoSuchMethod异常 就说明它找不到那个gettet方法 需要做个规范
					 */
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));

					String val = (String) m.invoke(object);// 调用getter方法获取属性值
					if (val != null) {
						// System.out.println("String type:" + val);
						list.add(val);
					} else {
						list.add("");
					}

				}

				// 如果类型是Integer
				if (field.getGenericType().toString().equals("class java.lang.Integer")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Integer val = (Integer) m.invoke(object);
					if (val != null) {
						// System.out.println("Integer type:" + val);
						list.add(val.toString());
					} else {
						list.add("");
					}

				}

				// 如果类型是Double
				if (field.getGenericType().toString().equals("class java.lang.Double")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Double val = (Double) m.invoke(object);
					if (val != null) {
						// System.out.println("Double type:" + val);
						list.add(val.toString());
					} else {
						list.add("");
					}

				}

				// 如果类型是Boolean 是封装类
				if (field.getGenericType().toString().equals("class java.lang.Boolean")) {
					Method m = (Method) object.getClass().getMethod(field.getName());
					Boolean val = (Boolean) m.invoke(object);
					if (val != null) {
						// System.out.println("Boolean type:" + val);
						list.add(val.toString());
					} else {
						list.add("");
					}

				}

				// 如果类型是boolean 基本数据类型不一样 这里有点说名如果定义名是 isXXX的 那就全都是isXXX的
				// 反射找不到getter的具体名
				if (field.getGenericType().toString().equals("boolean")) {
					Method m = (Method) object.getClass().getMethod(field.getName());
					Boolean val = (Boolean) m.invoke(object);
					if (val != null) {
						// System.out.println("boolean type:" + val);
						list.add(val.toString());
					} else {
						list.add("");
					}

				}
				// 如果类型是Date
				if (field.getGenericType().toString().equals("class java.util.Date")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Date val = (Date) m.invoke(object);
					if (val != null) {
						// System.out.println("Date type:" + val);
						list.add(val.toString());
					} else {
						list.add("");
					}

				}
				// 如果类型是Short
				if (field.getGenericType().toString().equals("class java.lang.Short")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Short val = (Short) m.invoke(object);
					if (val != null) {
						// System.out.println("Short type:" + val);
						list.add(val.toString());
					} else {
						list.add("");
					}
				}
				// 如果还需要其他的类型请自己做扩展
			}
		} else {
			System.err.println("object is null");
		}
		return list;
	}

	// 把一个字符串的第一个字母大写;
	private static String getMethodName(String fildeName) throws Exception {
		if (fildeName.charAt(0) >= 'a' && fildeName.charAt(0) <= 'z') {
			byte[] items = fildeName.getBytes();
			items[0] = (byte) ((char) items[0] - 'a' + 'A');
			return new String(items);
		} else {
			return fildeName;
		}
	}

	//点击事件;
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		//获取行列;
        int []columns = table.getSelectedColumns();
        int column=columns[0];
        int row = table.getSelectedRow();
        //System.out.println("!!!!!!!!!!"+row+"      "+column);
        //改变复选框的选择状态,并添加或删除delList中的数据;
        if(column==TableTitles.length-1){
        	if((Boolean)table.getValueAt(row, column)){
        		table.setValueAt(false, row, column);
        		
				for(int i=0;i<delList.size();i++){
				    if(delList.get(i)==row)
				    	delList.remove(i);
				}
        		
        	}
        	else{
        		table.setValueAt(true, row, column);
        		delList.add(row);
        	}
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}

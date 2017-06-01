package zadanie2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Database {

	private Connection connection;
	private TravelData data;
	private static String columnNames[] = { "Country", "Departure", "Arrival", "Landscape", "Price", "Currency"};
	private String[] tableName = new String[]{"oferta", "oferta1"};
	private TableModel model = null;
	private JTable table = null;
	private String[] locLanguage = new String[]{"pl_PL" , "en_US"};

	public Database(String url, TravelData data){
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connection = DriverManager.getConnection(url);
			this.data = data;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void create(){
		try {
			Statement stm = null;
			String defLocale = System.getProperty("user.language") + "_" + System.getProperty("user.country"); 

			/*Statement r =  connection.createStatement();
			r.executeUpdate("drop table oferta");
			r.executeUpdate("drop table oferta1");*/

			for(int i = 0; i < locLanguage.length; i++){
				String query = "";
				List<String> offers = data.getOffersDescriptionsList(locLanguage[i], "yyyy-MM-dd");
				query = "CREATE TABLE " + tableName[i] + "(" +						
						"country VARCHAR(64), " + 
						"departure VARCHAR(64), " +
						"arrival VARCHAR(64), " + 
						"place VARCHAR(64), " +
						"price VARCHAR(64), " + 
						"currency VARCHAR(64))";		
				stm = connection.createStatement();
				stm.execute(query);
				stm.close();			
				for(String offer : offers){

					String country = offer.split("[0-9]+")[0];
					offer = offer.substring(offer.indexOf(country) + country.length(), offer.length());

					query = "INSERT INTO  "  + tableName[i] + " " +
							"VALUES(" + 
							"'" + country + "'" + ", " + 
							"'" + offer.split(" ")[0] + "'" + ", " +  
							"'" + offer.split(" ")[1] + "'" + ", " +  
							"'" + offer.split(" ")[2] + "'" + ", " +  
							"'" + offer.split(" ")[3] + "'" + ", " +  
							"'" + offer.split(" ")[4] + "'" + ")";
					stm = connection.createStatement();
					stm.execute(query);
					stm.close();
				}


			}	

		} catch (SQLException e) {
			System.out.println("tables already exists");
		}
	}

	public void showGui(){

		JFrame frame = new JFrame("Database");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 400));
		frame.pack();
		frame.setLocationRelativeTo(null);		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		model = new DefaultTableModel(getRowData(tableName[0]), columnNames);
		table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);
		panel.add(scroll, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel();
		JList<String> langList = new JList<>(new String[]{"pl", "en"});
		langList.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				String v = "";
				if(langList.getSelectedValue().equals("pl")){
					v = "oferta";
				} else {
					v = "oferta1";
				}
				String[][] tab = getRowData(v);				
				for(int i = 0; i < tab.length; i++){
					for(int j = 0; j < tab[i].length; j++){	
						model.setValueAt(tab[i][j], i, j);
					}
				}
				
				
			}			
		});
		panel2.add(langList, BorderLayout.EAST);
		
		panel.add(panel2, BorderLayout.NORTH);
		frame.add(panel);	
		
		frame.setVisible(true);
	}
	
	private String[][] getRowData(String table){
		
		String query = "select * from " + table;
		List<String[]> list = new ArrayList<>();
		
		try {
			Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery(query);
			String[] tab = null;
			while(rs.next()){
				tab = new String[6];
				tab[0] = rs.getString("country");
				tab[1] = rs.getString("departure");
				tab[2] = rs.getString("arrival");
				tab[3] = rs.getString("place");
				tab[4] = rs.getString("price");
				tab[5] = rs.getString("currency");		
				list.add(tab);
			}			
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[][] tab = new String[list.size()][];
		int i = 0;
		for(String[] s : list){
			tab[i] = s;
			i++;
		}
		return tab;
	}

}







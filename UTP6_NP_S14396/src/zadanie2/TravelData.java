package zadanie2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TravelData {
	
	private BufferedReader br = null;
	private List<String> offers = new ArrayList<>();
	private String[] countries = Locale.getISOCountries();
	private ResourceBundle landscapeBundle = null;

	public TravelData(File file){
		readData(file);
	}
	
	private void readData(File file){
		String value = "";
		for(File f : file.listFiles()){
			try {
				br = new BufferedReader(new FileReader(f));
				while((value = br.readLine()) != null){
					offers.add(value);
				}
				value = "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	public List<String> getOffersDescriptionsList(String loc, String dateFormat){
		
		String language = loc.split("_")[0];
		String country = loc.split("_")[1];
		Locale currentLocale = new Locale(language, country);
		
		String[] offer = new String[7];
						
		List<String> list = new ArrayList<>();
			
		for(String s : offers){
			int i = 0;
			for(String z : s.split("\t")){
				offer[i] = z;
				i++;
			}
			
			Locale offerLocale = null;
			if(offer[0].contains("_")){
				offerLocale = new Locale(offer[0].split("_")[0], offer[0].split("_")[1]);
			} else {
				offerLocale = new Locale(offer[0]);
			}
			 
			for(String z : countries){
				Locale obj = new Locale("", z);
				if(obj.getDisplayCountry(offerLocale).equals(offer[1])){
					if(!currentLocale.getDisplayLanguage().equals(offerLocale.getDisplayLanguage())){
						obj = new Locale("", obj.getCountry());
						offer[1] = obj.getDisplayCountry(currentLocale);
					}
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			try {
				Date date = sdf.parse(offer[2]);
				offer[2] = sdf.format(date);
				date = sdf.parse(offer[3]);
				offer[3] = sdf.format(date);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			landscapeBundle = ResourceBundle.getBundle("zadanie2.landscape", offerLocale);
			offer[4] = landscapeBundle.getString(offer[4]);
			landscapeBundle = ResourceBundle.getBundle("zadanie2.landscape", currentLocale);
			offer[4] = landscapeBundle.getString(offer[4]);

			NumberFormat parser = NumberFormat.getInstance(offerLocale);
			NumberFormat formatter = NumberFormat.getInstance(currentLocale);
			try {
				Number number = parser.parse(offer[5]);
				offer[5] = formatter.format(number.doubleValue());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			list.add(createOffer(offer));	
			
		}		
		return list;		
	}
	
	public String createOffer(String[] offer){
		
		StringBuilder result = new StringBuilder();
		
		for(int i = 1; i < offer.length; i++){
			result.append(offer[i] + " ");
		}
		
		return result.toString();
	}
	
	public List<String> getOffers(){
		return offers;
	}
}

package fr.upem.lgtools.text;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {
	
	
	private static String feats(Map<String,String> feats){
		StringBuilder sb = new StringBuilder();
		if(feats.size() == 0){
			return "_";
		}
		for(String att:feats.keySet()){
			sb.append("|").append(att).append("=").append(feats.get(att));			
		}
		sb.deleteCharAt(0);
		return sb.toString();
		
	}
	
	
	private static void writeUnit(BufferedWriter out, Unit u) throws IOException{
		out.write(u.getId()+"\t"+u.getForm()+"\t"+u.getLemma()+"\t"+u.getCpos());
		out.write("\t"+u.getPos()+"\t"+feats(u.getFeatures()));
		out.write("\t"+u.getSheadId()+"\t"+u.getSlabel());
		out.write("\t"+u.getGoldSheadId()+"\t"+u.getGoldSlabel());
		out.write("\n");
		
	}
	
	private static void writeUnitInXConll(BufferedWriter out, Unit u) throws IOException{
		out.write(u.getId()+"\t"+Arrays.toString(u.getPositions())+"\t"+u.getForm()+"\t"+u.getLemma()+"\t"+u.getCpos());
		out.write("\t"+u.getPos()+"\t"+feats(u.getFeatures()));
		out.write("\t"+u.getSheadId()+"\t"+u.getSlabel());
		out.write("\t"+u.getGoldSheadId()+"\t"+u.getGoldSlabel());
		out.write("\t"+u.getLheadId()+"\t"+u.getGoldLHead());
		out.write("\n");
		
	}
	
	
	public static void saveTreebankInConll(DepTreebank tb,String filename) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		for(Sentence s:tb){
			for(Unit u:s.getTokens()){
				writeUnit(out,u);
			}
			out.write("\n");
		}
		out.close();			
	}
	
	public static void saveTreebankInXConll(DepTreebank tb,String filename) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		for(Sentence s:tb){
			for(Unit u:s.getUnits()){
				writeUnitInXConll(out,u);
			}
			out.write("\n");
		}
		out.close();			
	}
	
	
	private static boolean isCrossing(int i1, int j1, int i2, int j2){
		int tmp = i1;
		i1 = Math.min(i1, j1);
		j1 = Math.max(tmp,j1);
		tmp = i2;
		i2 = Math.min(i2, j2);
		j2 = Math.max(tmp,j2);
		//System.err.println(i1+","+j1+":"+i2+","+j2);
		if(i1 < i2 && i2 < j1 && j1 < j2){
			return true;
		}
		return false;
	}
	
	
	
	
	
	public static boolean isProjectiveSentence(Sentence sentence){
		//Necessary condition: fixed MWEs must be contiguous
		
		for(Unit u:sentence.getTokenSequence(true)){
			int[] positions = u.getPositions();
			Arrays.sort(positions);
			int pos0 = positions[0];
			for(int i = 1; i < positions.length ; i++){
				int pos1 = positions[i];
				if(pos1 != pos0 + 1){
					System.err.println("Sentence with non-contiguous fixed expressions");
					return false;
				}
				pos0 = pos1;
				
			}
			
		}
		
		//Standard non-projectivity test
		for(Unit u1:sentence.getTokenSequence(true)){
			for(Unit u2:sentence.getTokenSequence(true)){
				if(u1 != u2){
					
					int j1 = sentence.get(u1.getGoldSheadId()).getUnitFirstTokenPosition();
					Unit u = sentence.get(u2.getGoldSheadId());
					//System.err.println(u2);
					int j2 = u.getUnitFirstTokenPosition();
					int i1 = u1.getUnitFirstTokenPosition();
					int i2 = u2.getUnitFirstTokenPosition();
					if(isCrossing(i1,j1,i2,j2)){
						//System.err.println(u1.getGoldSheadId());
						  //System.err.println(u1+"--"+i1+" "+j1);
						  //System.err.println(u2+"--"+i2+" "+j2);
						  //System.err.println(sentence);
						System.err.println("Non projective sentence (class Utils)");
								return false;
					}
					
				}

			}

		}
		return true;
	}

	
	
	//for now, it only deals with MWE component positions and not POS of the MWE 
	
		/**
		 * 
		 * 
		 * 
		 * @param mwePositions
		 * @param s
		 * @return the existing mwe unit, retun null if not found
		 */
		
		public static Unit findExistingMweUnitByPosition(int[] mwePositions, List<Unit> units){
			for(Unit u:units){
				if(u != null && Arrays.equals(u.getPositions(), mwePositions)){
					return u;
				}
			}
			
			return null;
		}
	
	public static Unit mergeUnitsAndAdd(Unit u1, Unit u2,List<Unit> units){
		String form = u1.getForm()+"_"+u2.getForm();
		String lemma = u1.getLemma()+"_"+u2.getLemma();
		String cat = u1.getPos()+"_"+u2.getPos();
		int [] pos1 = u1.getPositions();
		int [] pos2 = u2.getPositions();
		int[] positions = new int[pos1.length+pos2.length];
		
		// fill positions
		for(int i = 0 ; i < pos1.length ; i++){
			positions[i] = pos1[i];
		}
		for(int i = 0 ; i < pos2.length ; i++){
			positions[i+pos1.length] = pos2[i];
		}
		
		
		int id = units.size() + 1;
		//System.err.println(form);
		//System.err.println(Arrays.toString(positions));
		//System.err.println(units);
		
		
		Unit mwe = findExistingMweUnitByPosition(positions, units);
		//System.err.println(mwe);
		
		
		if(mwe == null){
			mwe = new Unit(id,form, positions);
		      units.add(mwe);
		}
		mwe.setLemma(lemma);
		
		mwe.setPos(cat);
		return mwe;
	}
	
}

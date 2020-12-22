import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;


public class Test {
	public Test() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		String str = "黑胡桃木家具";
		Result res = ToAnalysis.parse(str);
		System.out.println(res.getTerms().get(0).getRealName());
	}
}

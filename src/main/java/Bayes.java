import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.util.StringUtils;

public class Bayes {
	private static Map<String, String> trainData = new HashMap();
	private static Map<String, Double> pType = new HashMap();
	private static Map<CharType, Double> pCharType = new HashMap();

	private static void readTrainData() throws Throwable {
		String path = "/Users/zhaoyuntao/Documents/货物分类.csv";
		File f = new File(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(f), "GBK"));
		String line = null;
		int n = 0;
		while ((line = reader.readLine()) != null) {
			String[] words = line.split(";");
			if (words.length >= 2) {
				if (trainData.containsKey(words[0])) {
					System.out.println(words[0]);
					n++;
				}
				trainData.put(words[0], words[1]);
			}

		}
	}

	private static void trainModel() {
		// 每个类型出现的概率
		int allCount = 0;
		Set<String> uniqueValues = new HashSet();
		Map<String, Integer> valueCount = new HashMap();
		for (String value : trainData.values()) {
			allCount++;
			uniqueValues.add(value);
			Integer count = valueCount.get(value);
			valueCount.put(value, count != null ? count + 1 : 1);
		}
		for (String value : uniqueValues) {
			pType.put(value, valueCount.get(value) / (double) allCount);
		}

		// 先分词，看每个词对应类型的概率
		Map<CharType, Integer> charTypeCount = new HashMap();
		for (String key : trainData.keySet()) {
			Result tokenize = ToAnalysis.parse(key);
			for (Term c :tokenize.getTerms()) {
				String s = c.getName();
				if (StringUtils.isEmpty(s)) {
					continue;
				}
				CharType ct = new CharType();
				ct.c = s;
				ct.type = trainData.get(key);
				Integer num = charTypeCount.get(ct);
				charTypeCount.put(ct, num != null ? num + 1 : 1);
			}
		}
		for (CharType ct : charTypeCount.keySet()) {
			pCharType.put(ct,
					charTypeCount.get(ct) / (double) valueCount.get(ct.type));
		}

	}

	private static Object[] detectType(String name) {
		double mostP = 0;
		String mostValue = "无法判别";
		Set<String> types = pType.keySet();

		for (String type : types) {
			Double result = null;
			Result tokenize = ToAnalysis.parse(name);
			for (Term c : tokenize.getTerms()) {
				CharType ct = new CharType();
				ct.c = c.getName();
				ct.type = type;
				Double d = pCharType.get(ct);
				if (d == null) {
					continue;
				} else {
					if (result == null) {
						result = d;
					} else {
						result = result * d;
					}
				}
			}
			if (result != null) {
				result = result * pType.get(type);
				if (result > mostP) {
					mostP = result;
					mostValue = type;
				}
			}
		}
		return new Object[]{mostValue,mostP};
	}

	public static void main(String[] args) throws Throwable {
		System.out.println("---正在初始化模型---");
		readTrainData();
		trainModel();
		System.out.println("---模型初始化完成，开始预测---");
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("输入货物名称:");
			String line = scanner.nextLine();
			if ("exit".equals(line)) {
				break;
			}
			Object[] result = detectType(line);
			System.out.println("推测货物类型为:" + result[0]+",概率为："+result[1]);

		}
		scanner.close();

	}

	private static class CharType {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((c == null) ? 0 : c.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CharType other = (CharType) obj;
			if (c == null) {
				if (other.c != null)
					return false;
			} else if (!c.equals(other.c))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

		String c;
		String type;
	}
}

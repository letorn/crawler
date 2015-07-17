package util;

public class Ver {

	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim()) ? true : false;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static void main(String[] args) {
		String str = "system&004&养老保险$$system&005&失业保险$$system&006&医疗保险$$system&007&生育保险$$system&008&工伤保险$$self&双休$$system&028&带薪年假$$system&038&入职培训$$system&012&员工培训$$system&010&年终奖$$system&021&交通补贴$$system&011&员工旅游";
		System.out.println(str.replaceAll("\\d|system|self|&", "").replaceAll("\\$\\$", " "));
		String a = "养老保险 失业保险 医疗保险 生育保险 工伤保险 双休 带薪年假 入职培训 员工培训 年终奖 交通补贴 员工旅游";
	}
	
}

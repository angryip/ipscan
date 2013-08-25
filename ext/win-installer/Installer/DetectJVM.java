public class DetectJVM {
	private static final String keys[] = {
			"sun.arch.data.model",
			"com.ibm.vm.bitmode",
			"os.arch",
	};

	public static void main(String[] args) {
		boolean print = args.length > 0 && "-print".equals(args[0]);
		for (String key : keys) {
			String property = System.getProperty(key);
			if (print) System.out.println(key + "=" + property);
			if (property != null) {
				int errCode = (property.indexOf("64") >= 0) ? 64 : 32;
				if (print) System.out.println("err code=" + errCode);
				System.exit(errCode);
			}
		}
	}
}

package polite.util;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;

public abstract class Logger {

	private static Properties debugProperty = new Properties();
	private static final Calendar calendar = Calendar.getInstance();
	private static final String CLASS_NAME = Logger.class.getCanonicalName();
	private static final String PROPS_DEBUG_PROPS = "polite.debug.props";
	private static final String PROPS_DEBUG = "polite.debugging";
	private static final String PROPS_TIME_FORMAT = CLASS_NAME + ".timeFormat";
	private static final String PROPS_LOG_FILE = "polite.debuging.output";
	private static final String default_time_format = "HH:mm:ss";
	private static String time_format;
	private static final SimpleDateFormat sdf;
	private static final boolean propertiesAddable;
	private static boolean propertiesAdded = false;

	private static boolean fDebug;
	private static PrintStream out;

	static {
		final String propsFilePath = System.getProperty(PROPS_DEBUG_PROPS);
		if (propsFilePath != null) {
			propertiesAddable = true;
			try {
				debugProperty.load(new FileReader(new File(propsFilePath)));
			} catch (Exception e) {
				System.err.println("Cannot locate properties file");
			}
		} else {
			propertiesAddable = false;
		}
		time_format = getProperty(PROPS_TIME_FORMAT);
		if (time_format == null) {
			time_format = default_time_format;
			addProperty(PROPS_TIME_FORMAT, time_format);
		}
		sdf = new SimpleDateFormat(time_format);
		fDebug = Boolean.parseBoolean(getProperty(PROPS_DEBUG));
		if (!hasProperty(PROPS_DEBUG)) {
			addProperty(PROPS_DEBUG, "" + fDebug);
		}

		final String logFilePath = getProperty(PROPS_LOG_FILE);
		try {
			if (logFilePath != null && !logFilePath.equals("")) {
				File file = new File(logFilePath);
				if (file.exists() && file.canWrite() || file.createNewFile()
						&& file.canWrite())
					out = new PrintStream(file);
			} else {
				addProperty(PROPS_LOG_FILE,"");
			}
		} catch (Exception e) {
			System.err
					.println("Cannot create log file for output, default to stdout");
		} finally {
			if (out == null)
				out = System.out;
		}
	}

	private static void addProperty(String propName, String value) {
		propertiesAdded = true;
		debugProperty.setProperty(propName, value);
	}

	private static void addProperty(String c, String m, String value) {
		addProperty(getCM(c, m), value);
	}

	private static boolean hasProperty(String propName) {
		return debugProperty.contains(propName);
	}

	private static boolean hasProperty(String c, String m) {
		return hasProperty(getCM(c, m));
	}

	private static String getProperty(String propName) {
		return debugProperty.getProperty(propName);
	}

	private static String getCM(String c, String m) {
		return c + "-" + m;
	}

	private static String getProperty(String c, String m) {
		return getProperty(getCM(c, m));
	}

	public static boolean hasPropertiesAdded() {
		return propertiesAdded;
	}

	public static boolean debugEnabled() {
		return fDebug;
	}

	public static void setDebug(boolean flag) {
		fDebug = flag;
	}

	public static PrintStream setDebugOut(PrintStream ps) {
		PrintStream oldout = out;
		out = ps;
		return oldout;
	}

	private static String PRINT_CLASS_MSG_FORMAT = "%s - %s\n >>> %s\n";
	private static String PRINT_METHOD_MSG_FORMAT = "%s - %s-%s\n\t>>> %s\n";

	private static void logMessage(String className, String msgName, String msg) {
		if (fDebug) {
			if (className == null && className == "") {
				return;
			}
			if (msgName != null) {
				if (!hasProperty(className, msgName)) {
					String s = getProperty(className);
					if(s==null) addProperty(className, debugEnabled() + "");
					addProperty(className, msgName, getProperty(className));
				}
				if (!Boolean.parseBoolean(getProperty(className, msgName))) {
					return;
				}
			} else {
				if (!hasProperty(className)) {
					addProperty(className, debugEnabled() + "");
				}
				if (!Boolean.parseBoolean(getProperty(className))) {
					return;
				}
			}
			if (msgName == null) {
				out.printf(PRINT_CLASS_MSG_FORMAT, now(), className, msg);
			} else {
				out.printf(PRINT_METHOD_MSG_FORMAT, now(), className, msgName,
						msg);
			}
		}
	}

	private static String now() {
		return sdf.format(calendar.getTime());
	}

	public static Logger getClassLogger(Class<?> cClass) {
		return new ClassLogger(cClass);
	}

	public static Logger getMethodLogger(Class<?> className, String methodName) {
		return new MethodLogger(className, methodName);
	}

	abstract public Logger getMethodLogger(String msg);

	abstract public void log(String msg);

	abstract public void enter();

	abstract public void leave();

	public static class ClassLogger extends Logger {
		private Class<?> cmClass;

		public ClassLogger(Class<?> cClass) {
			cmClass = cClass;
		}

		protected String getClassName() {
			return cmClass.getCanonicalName();
		}

		public void log(String s) {
			logMessage(getClassName(), null, s);
		}

		@Override
		public void enter() {
		}

		@Override
		public void leave() {
		}

		@Override
		public Logger getMethodLogger(String methodName) {
			return new MethodLogger(cmClass, methodName);
		}
	}

	public static class MethodLogger extends ClassLogger {
		private final String methodname;

		public MethodLogger(Class<?> className, String methodName) {
			super(className);
			methodname = methodName;
		}

		@Override
		public void enter() {
			log("Enter");
		}

		@Override
		public void leave() {
			log("Leave");

		}

		@Override
		public void log(String msg) {
			logMessage(getClassName(), methodname, msg);
		}
	}

	public static void writePropertyFile() {
		if (!propertiesAddable)
			return;

		File file = new File(System.getProperty(PROPS_DEBUG_PROPS));
		try {
			if (!file.exists())
				file.createNewFile();
			if (file.canWrite()) {
				PrintStream o = new PrintStream(file);
				o.print("######################\n");
				o.print("#  Debug properties\n");
				o.print("######################"+'\n');
				o.print(removeProp(PROPS_DEBUG)+'\n');
				o.print(removeProp(PROPS_LOG_FILE)+'\n');
				o.print('\n');
				o.print("######################"+'\n');
				o.print("#  Time print format"+'\n');
				o.print("######################"+'\n');
				o.print(removeProp(PROPS_TIME_FORMAT)+'\n');
				o.print('\n');
				o.print("######################"+'\n');
				o.print("# Method List\n");
				o.print("######################"+'\n');
				Object[] list = debugProperty.keySet().toArray();
				Arrays.sort(list);
				for (Object s : list) {
					o.print(removeProp(s.toString())+'\n');
				}
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fail to write to property file "
					+ System.getProperty(PROPS_DEBUG_PROPS));

		}
	}

	private static String removeProp(String propName) {
		String val = debugProperty.remove(propName).toString();
		return propName + "=" + (val == null ? "" : val);
	}

}

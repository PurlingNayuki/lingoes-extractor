package cn.kk.extractor.lingoes;

/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.swing.filechooser.FileSystemView;

public final class Helper {
    public static boolean DEBUG = false;

    public static final int BUFFER_SIZE = 1024 * 1024 * 4;
    public static final Charset CHARSET_EUCJP = Charset.forName("EUC-JP");
    public static final Charset CHARSET_UTF16LE = Charset.forName("UTF-16LE");
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    public final static String EMPTY_STRING = "";
    public final static List<String> EMPTY_STRING_LIST = Collections.emptyList();

    private static final String[][] HTML_ENTITIES = {
            {
                    "amp", "38"
            }, {
                    "quot", "34"
            }, {
                    "lt", "60"
            }, {
                    "gt", "62"
            }, {
                    "fnof", "402"
            }, {
                    "Alpha", "913"
            }, {
                    "Beta", "914"
            }, {
                    "Gamma", "915"
            }, {
                    "Delta", "916"
            }, {
                    "Epsilon", "917"
            }, {
                    "Zeta", "918"
            }, {
                    "Eta", "919"
            }, {
                    "Theta", "920"
            }, {
                    "Iota", "921"
            }, {
                    "Kappa", "922"
            }, {
                    "Lambda", "923"
            }, {
                    "Mu", "924"
            }, {
                    "Nu", "925"
            }, {
                    "Xi", "926"
            }, {
                    "Omicron", "927"
            }, {
                    "Pi", "928"
            }, {
                    "Rho", "929"
            }, {
                    "Sigma", "931"
            }, {
                    "Tau", "932"
            }, {
                    "Upsilon", "933"
            }, {
                    "Phi", "934"
            }, {
                    "Chi", "935"
            }, {
                    "Psi", "936"
            }, {
                    "Omega", "937"
            }, {
                    "alpha", "945"
            }, {
                    "beta", "946"
            }, {
                    "gamma", "947"
            }, {
                    "delta", "948"
            }, {
                    "epsilon", "949"
            }, {
                    "zeta", "950"
            }, {
                    "eta", "951"
            }, {
                    "theta", "952"
            }, {
                    "iota", "953"
            }, {
                    "kappa", "954"
            }, {
                    "lambda", "955"
            }, {
                    "mu", "956"
            }, {
                    "nu", "957"
            }, {
                    "xi", "958"
            }, {
                    "omicron", "959"
            }, {
                    "pi", "960"
            }, {
                    "rho", "961"
            }, {
                    "sigmaf", "962"
            }, {
                    "sigma", "963"
            }, {
                    "tau", "964"
            }, {
                    "upsilon", "965"
            }, {
                    "phi", "966"
            }, {
                    "chi", "967"
            }, {
                    "psi", "968"
            }, {
                    "omega", "969"
            }, {
                    "thetasym", "977"
            }, {
                    "upsih", "978"
            }, {
                    "piv", "982"
            }, {
                    "bull", "8226"
            }, {
                    "hellip", "8230"
            }, {
                    "prime", "8242"
            }, {
                    "Prime", "8243"
            }, {
                    "oline", "8254"
            }, {
                    "frasl", "8260"
            }, {
                    "weierp", "8472"
            }, {
                    "image", "8465"
            }, {
                    "real", "8476"
            }, {
                    "trade", "8482"
            }, {
                    "alefsym", "8501"
            }, {
                    "larr", "8592"
            }, {
                    "uarr", "8593"
            }, {
                    "rarr", "8594"
            }, {
                    "darr", "8595"
            }, {
                    "harr", "8596"
            }, {
                    "crarr", "8629"
            }, {
                    "lArr", "8656"
            }, {
                    "uArr", "8657"
            }, {
                    "rArr", "8658"
            }, {
                    "dArr", "8659"
            }, {
                    "hArr", "8660"
            }, {
                    "forall", "8704"
            }, {
                    "part", "8706"
            }, {
                    "exist", "8707"
            }, {
                    "empty", "8709"
            }, {
                    "nabla", "8711"
            }, {
                    "isin", "8712"
            }, {
                    "notin", "8713"
            }, {
                    "ni", "8715"
            }, {
                    "prod", "8719"
            }, {
                    "sum", "8721"
            }, {
                    "minus", "8722"
            }, {
                    "lowast", "8727"
            }, {
                    "radic", "8730"
            }, {
                    "prop", "8733"
            }, {
                    "infin", "8734"
            }, {
                    "ang", "8736"
            }, {
                    "and", "8743"
            }, {
                    "or", "8744"
            }, {
                    "cap", "8745"
            }, {
                    "cup", "8746"
            }, {
                    "int", "8747"
            }, {
                    "there4", "8756"
            }, {
                    "sim", "8764"
            }, {
                    "cong", "8773"
            }, {
                    "asymp", "8776"
            }, {
                    "ne", "8800"
            }, {
                    "equiv", "8801"
            }, {
                    "le", "8804"
            }, {
                    "ge", "8805"
            }, {
                    "sub", "8834"
            }, {
                    "sup", "8835"
            }, {
                    "sube", "8838"
            }, {
                    "supe", "8839"
            }, {
                    "oplus", "8853"
            }, {
                    "otimes", "8855"
            }, {
                    "perp", "8869"
            }, {
                    "sdot", "8901"
            }, {
                    "lceil", "8968"
            }, {
                    "rceil", "8969"
            }, {
                    "lfloor", "8970"
            }, {
                    "rfloor", "8971"
            }, {
                    "lang", "9001"
            }, {
                    "rang", "9002"
            }, {
                    "loz", "9674"
            }, {
                    "spades", "9824"
            }, {
                    "clubs", "9827"
            }, {
                    "hearts", "9829"
            }, {
                    "diams", "9830"
            }, {
                    "OElig", "338"
            }, {
                    "oelig", "339"
            }, {
                    "Scaron", "352"
            }, {
                    "scaron", "353"
            }, {
                    "Yuml", "376"
            }, {
                    "circ", "710"
            }, {
                    "tilde", "732"
            }, {
                    "ensp", "8194"
            }, {
                    "emsp", "8195"
            }, {
                    "thinsp", "8201"
            }, {
                    "zwnj", "8204"
            }, {
                    "zwj", "8205"
            }, {
                    "lrm", "8206"
            }, {
                    "rlm", "8207"
            }, {
                    "ndash", "8211"
            }, {
                    "mdash", "8212"
            }, {
                    "lsquo", "8216"
            }, {
                    "rsquo", "8217"
            }, {
                    "sbquo", "8218"
            }, {
                    "ldquo", "8220"
            }, {
                    "rdquo", "8221"
            }, {
                    "bdquo", "8222"
            }, {
                    "dagger", "8224"
            }, {
                    "Dagger", "8225"
            }, {
                    "permil", "8240"
            }, {
                    "lsaquo", "8249"
            }, {
                    "rsaquo", "8250"
            }, {
                    "euro", "8364"
            },
    };
    private static final String[] HTML_KEYS;

    private static final int[] HTML_VALS;
    public final static int MAX_CONNECTIONS = 2;

    public final static String SEP_ATTRIBUTE = "‹";

    public final static String SEP_DEFINITION = "═";

    public final static String SEP_LIST = "▫";

    public final static String SEP_NEWLINE = "\n";

    public final static char SEP_NEWLINE_CHAR = '\n';

    public final static String SEP_PARTS = "║";

    public final static String SEP_PINYIN = "'";

    public static final String SEP_SAME_MEANING = "¶";

    public static final String SEP_SPACE = " ";

    public final static String SEP_WORDS = "│";

    public final static String SEP_ETC = "…";

    public static final byte[] SEP_DEFINITION_BYTES = Helper.SEP_DEFINITION.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] SEP_LIST_BYTES = Helper.SEP_LIST.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] SEP_WORDS_BYTES = Helper.SEP_WORDS.getBytes(Helper.CHARSET_UTF8);
    public static final int[] SEP_WORDS_INTS = ArrayHelper.toIntArray(Helper.SEP_WORDS.getBytes(Helper.CHARSET_UTF8));
    public static final byte[] SEP_ATTRS_BYTES = Helper.SEP_ATTRIBUTE.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] SEP_NEWLINE_BYTES = Helper.SEP_NEWLINE.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] SEP_PARTS_BYTES = Helper.SEP_PARTS.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] SEP_ETC_BYTES = Helper.SEP_ETC.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] SEP_URI_POSTFIX_BYTES = {
            ':', '/', '/'
    };
    public static final byte[] SEP_SPACE_BYTES = Helper.SEP_SPACE.getBytes(Helper.CHARSET_UTF8);
    public static final byte[] BYTES_XML_TAG_START = {
            '&', 'l', 't', ';'
    };
    public static final byte[] BYTES_XML_TAG_STOP = {
            '&', 'g', 't', ';'
    };

    static {
        System.setProperty("http.keepAlive", "false");
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        HttpURLConnection.setFollowRedirects(false);

        Arrays.sort(Helper.HTML_ENTITIES, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return o1[0].compareTo(o2[0]);
            }
        });
        HTML_KEYS = new String[Helper.HTML_ENTITIES.length];
        HTML_VALS = new int[Helper.HTML_ENTITIES.length];
        int i = 0;
        for (String[] pair : Helper.HTML_ENTITIES) {
            Helper.HTML_KEYS[i] = pair[0];
            Helper.HTML_VALS[i] = Integer.parseInt(pair[1]);
            i++;
        }
    }

    public static void add(Map<String, Integer> statMap, String key) {
        Integer counter = statMap.get(key);
        if (counter == null) {
            statMap.put(key, Integer.valueOf(1));
        } else {
            statMap.put(key, Integer.valueOf(counter.intValue() + 1));
        }
    }

    public static final String appendFileName(String file, String suffix) {
        int indexOf = file.indexOf('.');
        return file.substring(0, indexOf) + suffix + file.substring(indexOf);
    }

    public static String download(String url) throws IOException {
        return Helper.download(url, File.createTempFile("kkdl", null).getAbsolutePath(), true);
    }

    public static String download(String url, String to, boolean overwrite) throws IOException {
        if (Helper.DEBUG) {
            System.out.println("下载'" + url + "'到'" + to + "'。。。");
        }
        final File toFile = new File(to);
        if (!overwrite && toFile.exists() && (toFile.length() > 0)) {
            System.err.println("文件'" + to + "'已存在。跳过下载程序。");
            return null;
        } else {
            int retries = 0;
            DownloadThread test = null;
            while ((retries++ < 10) && !(test = new DownloadThread(to, url)).success) {
                try {
                    test.start();
                    test.join(60000);
                    test.interrupt();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if ((test != null) && !test.success) {
                throw new IOException("下载失败：" + to);
            }
            return to;
        }
    }

    private static class DownloadThread extends Thread {
        private final String to;
        private final String url;
        boolean success;

        public DownloadThread(String to, String url) {
            super();
            this.to = to;
            this.url = url;
            this.success = false;
        }

        @Override
        public void run() {
            OutputStream out = null;
            InputStream in = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(this.to));
                in = new BufferedInputStream(Helper.openUrlInputStream(this.url));
                Helper.write(in, out);
                this.success = true;
            } catch (IOException e) {
                new File(this.to).delete();
                e.printStackTrace();
                System.err.println("错误：" + e.toString());
            } finally {
                Helper.close(in);
                Helper.close(out);
            }
        }
    }

    private static final Map<String, String> DEFAULT_CONN_HEADERS = new HashMap<String, String>();

    static {
        Helper.resetConnectionHeaders();
    }

    public final static InputStream openUrlInputStream(final String url) throws IOException {
        return Helper.openUrlInputStream(url, false, null);
    }

    public static final void resetConnectionHeaders() {
        Helper.DEFAULT_CONN_HEADERS.clear();
        Helper.DEFAULT_CONN_HEADERS.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.1) Gecko/20100101 Firefox/10.0.1");
        Helper.DEFAULT_CONN_HEADERS.put("Cache-Control", "no-cache");
        Helper.DEFAULT_CONN_HEADERS.put("Pragma", "no-cache");
    }

    public static final void putConnectionHeader(final String key, final String value) {
        Helper.DEFAULT_CONN_HEADERS.put(key, value);
    }

    public static final InputStream openUrlInputStream(final String url, final boolean post, final String output)
            throws IOException {
        return Helper.getUrlConnection(url, post, output).getInputStream();
    }

    public final static HttpURLConnection getUrlConnection(final String url) throws IOException {
        return Helper.getUrlConnection(url, false, null);
    }

    public final static HttpURLConnection getUrlConnection(final String url, final boolean post, final String output)
            throws IOException {
        int retries = 0;
        while (true) {
            try {
                URL urlObj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000);
                if (post) {
                    conn.setRequestMethod("POST");
                }
                final String referer;
                final int pathIdx;
                if ((pathIdx = url.lastIndexOf('/')) > "https://".length()) {
                    referer = url.substring(0, pathIdx);
                } else {
                    referer = url;
                }
                conn.setRequestProperty("Referer", referer);
                final Set<String> keys = Helper.DEFAULT_CONN_HEADERS.keySet();
                for (String k : keys) {
                    final String value = Helper.DEFAULT_CONN_HEADERS.get(k);
                    if (value != null) {
                        conn.setRequestProperty(k, value);
                    }
                }
                conn.setUseCaches(false);
                if (output != null) {
                    conn.setDoOutput(true);
                    BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
                    out.write(output.getBytes(Helper.CHARSET_UTF8));
                    out.close();
                }
                return conn;
            } catch (Throwable e) {
                // 连接中断
                if (retries++ > 20) {
                    throw new IOException(e);
                } else {
                    try {
                        Thread.sleep((60 * retries * 1000) + ((int) Math.random() * 1000 * 60 * retries));
                    } catch (InterruptedException e1) {
                        System.err.println("异常中断：" + e1.toString());
                    }
                }
            }
        }
    }

    public static ByteBuffer compressFile(String rawFile, int level) throws IOException {
        InputStream in = new FileInputStream(rawFile);
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream(Helper.BUFFER_SIZE);
        Deflater def = new Deflater(level);
        OutputStream out = new DeflaterOutputStream(dataOut, def, Helper.BUFFER_SIZE);
        Helper.write(in, out);
        in.close();
        def.end();
        return ByteBuffer.wrap(dataOut.toByteArray());
    }

    public static ByteBuffer compressFile(String rawFile, String dictionaryFile, int level) throws IOException {
        InputStream in = new FileInputStream(rawFile);
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream(Helper.BUFFER_SIZE);
        Deflater def = new Deflater(level);
        def.setDictionary(Helper.readBytes(dictionaryFile).array());
        OutputStream out = new DeflaterOutputStream(dataOut, def, Helper.BUFFER_SIZE);
        Helper.write(in, out);
        in.close();
        return ByteBuffer.wrap(dataOut.toByteArray());
    }

    public static ByteBuffer decompressFile(String compressedFile) throws IOException {
        InflaterInputStream in = new InflaterInputStream(new FileInputStream(compressedFile));
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream(Helper.BUFFER_SIZE);
        Helper.write(in, dataOut);
        in.close();
        return ByteBuffer.wrap(dataOut.toByteArray());
    }

    public static ByteBuffer decompressFile(String compressedFile, String dictionaryFile) throws IOException {
        Inflater inf = new Inflater();
        InputStream in = new InflaterInputStream(new FileInputStream(compressedFile), inf, Helper.BUFFER_SIZE);
        System.out.println(in.read());
        if (inf.needsDictionary()) {
            try {
                inf.setDictionary(Helper.readBytes(dictionaryFile).array());
                ByteArrayOutputStream dataOut = new ByteArrayOutputStream(Helper.BUFFER_SIZE);
                Helper.write(in, dataOut);
                in.close();
                return ByteBuffer.wrap(dataOut.toByteArray());
            } catch (IllegalArgumentException e) {
                System.err.println("文件夹不存在!");
                return null;
            } finally {
                inf.end();
            }
        } else {
            System.err.println("不需要词典!");
            inf.end();
            return null;
        }
    }

    /**
     * 
     * @param text
     *            text to analyze
     * @param definitions
     *            array of array of definitions with the first element as its key
     * @return {cutted text, definition key } if found definition otherwise null
     */
    public static String[] findAndCut(String text, String[][] definitions) {
        String def = null;
        int indexOf;
        for (String[] defArray : definitions) {
            for (int g = 1; g < defArray.length; g++) {
                String k = defArray[g];
                if ((indexOf = text.indexOf(k)) != -1) {
                    def = defArray[0];
                    if ((indexOf + k.length()) == text.length()) {
                        text = text.substring(0, indexOf);
                    } else {
                        text = text.substring(0, indexOf) + text.substring(indexOf + k.length());
                    }
                    break;
                }
            }
        }
        if (def != null) {
            return new String[] {
                    text, def
            };
        } else {
            return null;
        }
    }

    public final static String formatDuration(final long duration) {
        final long v = Math.abs(duration);
        final long days = v / 1000 / 60 / 60 / 24;
        final long hours = (v / 1000 / 60 / 60) % 24;
        final long mins = (v / 1000 / 60) % 60;
        final long secs = (v / 1000) % 60;
        final long millis = v % 1000;
        StringBuilder out = new StringBuilder();
        if (days > 0) {
            out.append(days).append(':').append(Helper.padding(hours, 2, '0')).append(':')
                    .append(Helper.padding(mins, 2, '0')).append(":").append(Helper.padding(secs, 2, '0')).append(".")
                    .append(Helper.padding(millis, 3, '0'));
        } else if (hours > 0) {
            out.append(hours).append(':').append(Helper.padding(mins, 2, '0')).append(":")
                    .append(Helper.padding(secs, 2, '0')).append(".").append(Helper.padding(millis, 3, '0'));
        } else if (mins > 0) {
            out.append(mins).append(":").append(Helper.padding(secs, 2, '0')).append(".")
                    .append(Helper.padding(millis, 3, '0'));
        } else {
            out.append(secs).append(".").append(Helper.padding(millis, 3, '0'));
        }
        return out.toString();

    }

    public static final String formatSpace(final long limit) {
        if (limit < 1024L) {
            return limit + " B";
        } else if (limit < (1024L * 1024)) {
            return (Math.round(limit / 10.24) / 100.0) + " KB";
        } else if (limit < (1024L * 1024 * 1024)) {
            return (Math.round(limit / 1024 / 10.24) / 100.0) + " MB";
        } else if (limit < (1024L * 1024 * 1024 * 1024)) {
            return (Math.round(limit / 1024.0 / 1024.0 / 10.24) / 100.0) + " GB";
        } else {
            return (Math.round(limit / 1024.0 / 1024.0 / 1024.0 / 10.24) / 100.0) + " TB";
        }
    }

    public static boolean isEmptyOrNull(String text) {
        return (text == null) || text.isEmpty();
    }

    public final static boolean isNotEmptyOrNull(String text) {
        return (text != null) && (text.length() > 0);
    }

    private static boolean isNumber(char c) {
        return (c >= '0') && (c <= '9');
    }

    public final static String padding(long value, int len, char c) {
        return Helper.padding(String.valueOf(value), len, c);
    }

    public final static String padding(String text, int len, char c) {
        if ((text != null) && (len > text.length())) {
            char[] spaces = new char[len - text.length()];
            Arrays.fill(spaces, c);
            return new String(spaces) + text;
        } else {
            return text;
        }
    }

    public final static void precheck(String inFile, String outDirectory) {
        if (!new File(inFile).isFile()) {
            System.err.println("Could not read input file: " + inFile);
            System.exit(-100);
        }
        if (!(new File(outDirectory).isDirectory() || new File(outDirectory).mkdirs())) {
            System.err.println("Could not create output directory: " + outDirectory);
            System.exit(-101);
        }
    }

    public static ByteBuffer readBytes(String file) throws IOException {
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        FileChannel fChannel = new RandomAccessFile(file, "r").getChannel();
        fChannel.transferTo(0, fChannel.size(), Channels.newChannel(dataOut));
        fChannel.close();
        return ByteBuffer.wrap(dataOut.toByteArray());
    }

    public static String stripHtmlText(final String line, final boolean startOk) {
        final int count = line.length();
        final StringBuilder sb = new StringBuilder(count);
        boolean ok = startOk;
        char c;
        for (int i = 0; i < count; i++) {
            c = line.charAt(i);
            if (c == '>') {
                ok = true;
            } else if (c == '<') {
                ok = false;
            } else if (ok) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String stripWikiText(final String wiki) {
        final int length = wiki.length();
        // ==title==. remove ''', {}, [], [|(text)], {|}
        final StringBuilder sb = new StringBuilder(length);
        char cp;
        int countEquals = 0;
        int countQuos = 0;
        int countSpaces = 0;
        int linkOpened = -1;
        boolean externalOpened = false;
        char last = '\0';
        for (int i = 0; i < length; i++) {
            cp = wiki.charAt(i);
            if ((cp != ' ') && (countSpaces > 0)) {
                if ((last != ' ') && (last != '(')) {
                    sb.append(' ');
                    last = ' ';
                }
                countSpaces = 0;
            }
            if ((cp != '\'') && (countQuos == 1)) {
                sb.append('\'');
                last = '\'';
                countQuos = 0;
            }
            if ((cp != '=') && (countEquals == 1)) {
                sb.append('=');
                last = '=';
                countEquals = 0;
            }
            switch (cp) {
                case ' ':
                    if (externalOpened) {
                        linkOpened = -1;
                    }
                    countSpaces++;
                    continue;
                case '{':
                case '}':
                    continue;
                case '[':
                    linkOpened = i;
                    if (((i + 7) < length)
                            && (((wiki.charAt(i + 1) == 'h') && (wiki.charAt(i + 2) == 't')
                                    && (wiki.charAt(i + 3) == 't') && (wiki.charAt(i + 4) == 'p')) || ((wiki
                                    .charAt(i + 1) == 'f') && (wiki.charAt(i + 2) == 't') && (wiki.charAt(i + 3) == 'p')))) {
                        externalOpened = true;
                    }
                    continue;
                case '|':
                    linkOpened = -1;
                    continue;
                case ']':
                    if (linkOpened != -1) {
                        i = linkOpened;
                    }
                    linkOpened = -1;
                    externalOpened = false;
                    continue;
                case '\'':
                    countQuos++;
                    continue;
                case '=':
                    countEquals++;
                    continue;
            }
            if (linkOpened == -1) {
                sb.append(cp);
                last = cp;
            }
        }
        return sb.toString();
    }

    public static final String substringBetween(final String text, final String start, final String end) {
        return Helper.substringBetween(text, start, end, true);
    }

    public static final String substringBetween(final String text, final String start, final String end,
            final boolean trim) {
        final int nStart = text.indexOf(start);
        final int nEnd = text.indexOf(end, nStart + start.length() + 1);
        if ((nStart != -1) && (nEnd > nStart)) {
            if (trim) {
                return text.substring(nStart + start.length(), nEnd).trim();
            } else {
                return text.substring(nStart + start.length(), nEnd);
            }
        } else {
            return null;
        }
    }

    public static String substringBetweenEnclose(String text, String start, String end) {
        final int nStart = text.indexOf(start);
        final int nEnd = text.lastIndexOf(end);
        if ((nStart != -1) && (nEnd != -1) && (nEnd > (nStart + start.length()))) {
            return text.substring(nStart + start.length(), nEnd);
        } else {
            return null;
        }
    }

    public final static String substringBetweenLast(final String text, final String start, final String end) {
        return Helper.substringBetweenLast(text, start, end, true);
    }

    public final static String substringBetweenLast(final String text, final String start, final String end,
            final boolean trim) {
        int nEnd = text.lastIndexOf(end);
        int nStart = -1;
        if (nEnd > 1) {
            nStart = text.lastIndexOf(start, nEnd - 1);
        } else {
            return null;
        }
        if ((nStart < nEnd) && (nStart != -1) && (nEnd != -1)) {
            if (trim) {
                return text.substring(nStart + start.length(), nEnd).trim();
            } else {
                return text.substring(nStart + start.length(), nEnd);
            }
        } else {
            return null;
        }

    }

    public static String substringBetweenNarrow(String text, String start, String end) {
        final int nEnd = text.indexOf(end);
        int nStart = -1;
        if (nEnd != -1) {
            nStart = text.lastIndexOf(start, nEnd - 1);
        }
        if ((nStart < nEnd) && (nStart != -1) && (nEnd != -1)) {
            return text.substring(nStart + start.length(), nEnd);
        } else {
            return null;
        }
    }

    public static String toConstantName(String line) {
        final int count = line.length();
        StringBuilder sb = new StringBuilder(count);
        line = line.toUpperCase();
        for (int i = 0; i < count; i++) {
            char c = line.charAt(i);
            if ((c == '.') || (c == ':') || (c == '?') || (c == ',') || (c == '*') || (c == '=') || (c == '„')
                    || (c == '“') || (c == ')') || ((c >= '0') && (c <= '9')) || (c == '"') || (c == ';') || (c == '!')
                    || (c == '<') || (c == '>')) {
                // [:\.\?;\*=\)0-9";!<>„“]
                continue;
            } else if ((c == ' ') || (c == '\'') || (c == '-') || (c == '&') || (c == 'ß')) {
                // [ '\-&ß]
                sb.append('_');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static void unescape(Writer writer, String str, int firstAmp) throws IOException {
        writer.write(str, 0, firstAmp);
        int len = str.length();
        for (int i = firstAmp; i < len; i++) {
            char c = str.charAt(i);
            if (c == '&') {
                int nextIdx = i + 1;
                int semiColonIdx = str.indexOf(';', nextIdx);
                if (semiColonIdx == -1) {
                    while (Helper.isNumber(str.charAt(i + 1))) {
                        i++;
                    }
                    semiColonIdx = i + 1;
                }
                int amphersandIdx = str.indexOf('&', i + 1);
                if ((amphersandIdx != -1) && (amphersandIdx < semiColonIdx)) {
                    writer.write(c);
                    continue;
                }
                String entityContent = str.substring(nextIdx, semiColonIdx);
                int entityValue = -1;
                int entityContentLen = entityContent.length();
                if (entityContentLen > 0) {
                    if (entityContent.charAt(0) == '#') {
                        if (entityContentLen > 1) {
                            char isHexChar = entityContent.charAt(1);
                            try {
                                switch (isHexChar) {
                                    case 'X':
                                    case 'x': {
                                        entityValue = Integer.parseInt(entityContent.substring(2), 16);
                                        break;
                                    }
                                    default: {
                                        entityValue = Integer.parseInt(entityContent.substring(1), 10);
                                    }
                                }
                                if (entityValue > 0xFFFF) {
                                    entityValue = -1;
                                }
                            } catch (NumberFormatException e) {
                                entityValue = -1;
                            }
                        }
                    } else {
                        int idx = Arrays.binarySearch(Helper.HTML_KEYS, entityContent);
                        if (idx >= 0) {
                            entityValue = Helper.HTML_VALS[idx];
                        }
                    }
                } else {
                    writer.write(c);
                    continue;
                }

                if (entityValue > -1) {
                    writer.write(entityValue);
                }
                i = semiColonIdx;
            } else {
                writer.write(c);
            }
        }

    }

    public static String unescapeHtml(String str) {
        try {
            int firstAmp = str.indexOf('&');
            if (firstAmp < 0) {
                return str;
            }

            StringWriter writer = new StringWriter((int) (str.length() * 1.1));
            Helper.unescape(writer, str, firstAmp);

            return writer.toString();
        } catch (IOException ioe) {
            return str;
        }
    }

    public static final void writeBytes(final byte[] data, final String file) throws IOException {
        FileOutputStream f = new FileOutputStream(file);
        f.write(data);
        f.close();
    }

    public static final void write(final InputStream in, final OutputStream out) throws IOException {
        int len;
        while ((len = in.read(Helper.IO_BB.array())) > 0) {
            out.write(Helper.IO_BB.array(), 0, len);
        }
    }

    public static final void writeToFile(final InputStream in, final File file) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        int len;
        while ((len = in.read(Helper.IO_BB.array())) > 0) {
            out.write(Helper.IO_BB.array(), 0, len);
        }
        out.close();
    }

    public static final int deleteDirectory(File path) {
        int deleted = 0;
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleted += Helper.deleteDirectory(file);
                } else {
                    if (file.delete()) {
                        deleted++;
                    }
                }
            }
        }
        path.delete();
        return deleted;
    }

    public static final String[] getFileNames(final File[] files) {
        String[] filePaths = new String[files.length];
        int i = 0;
        for (File f : files) {
            // System.out.println((i + 1) + ". " + f.getAbsolutePath());
            filePaths[i++] = f.getAbsolutePath();
        }
        return filePaths;
    }

    public final static long getFilesSize(String[] files) {
        long size = 0;
        for (String f : files) {
            size += new File(f).length();
        }
        return size;
    }

    public static String substringAfterLast(String f, String str) {
        int idx = f.lastIndexOf(str);
        if (-1 != idx) {
            return f.substring(idx + str.length());
        }
        return null;
    }

    public static String substringAfter(String f, String str) {
        int idx = f.indexOf(str);
        if (-1 != idx) {
            return f.substring(idx + str.length());
        }
        return null;
    }

    public static final double toFixed(final double d, final double precision) {
        final double pow = Math.pow(10, precision);
        return Math.round(d * pow) / pow;
    }

    private static final FileSystemView FILE_SYSTEM = FileSystemView.getFileSystemView();

    public static final FileInputStream findResourceAsStream(final String resource) throws IllegalArgumentException,
            IOException {
        final File file = Helper.findResource(resource);
        if (null != file) {
            return new FileInputStream(file);
        } else {
            return null;
        }
    }

    /**
     * <pre>
     * Find resource in possible directories:
     * 1. find in the running directory
     * 2. find in the user directory
     * 3. find in the user document directory
     * 4. find on the user desktop
     * 5. get from root of the running directory
     * 6. load from class path and system path
     * 7. find in all root directories e.g. C:, D:
     * 8. find in temporary directory
     * </pre>
     * 
     * @param resource
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static final File findResource(final String resource) throws IllegalArgumentException {
        File resFile = null;
        if (new File(resource).isFile()) {
            // in run directory
            resFile = new File(resource);
        }
        if (resFile == null) {
            // in user directory
            final String dir = System.getProperty("user.home");
            if (!Helper.isEmptyOrNull(dir)) {
                if (new File(dir, resource).isFile()) {
                    resFile = new File(dir, resource);
                }
            }
        }
        if (resFile == null) {
            // in user document directory
            final File dir = Helper.FILE_SYSTEM.getDefaultDirectory();
            if (dir != null) {
                if (new File(dir, resource).isFile()) {
                    resFile = new File(dir, resource);
                }
            }
        }
        if (resFile == null) {
            // in user desktop directory
            final File dir = Helper.FILE_SYSTEM.getHomeDirectory();
            if (dir != null) {
                if (new File(dir, resource).isFile()) {
                    resFile = new File(dir, resource);
                }
            }
        }
        if (resFile == null) {
            // get from root of run directory
            final File dir = new File("/");
            if (dir.isDirectory()) {
                if (new File(dir, resource).isFile()) {
                    resFile = new File(dir, resource);
                }
            }
        }
        if (resFile == null) {
            // get from class path (root)
            URL resUrl = Helper.class.getResource("/" + resource);
            if (resUrl != null) {
                try {
                    resFile = File.createTempFile(resource, null);
                    Helper.writeToFile(Helper.class.getResourceAsStream("/" + resource), resFile);
                } catch (IOException e) {
                    System.err.println("从JAR导出'" + resource + "'时出错：" + e.toString());
                }
                if ((resFile != null) && !resFile.isFile()) {
                    resFile = null;
                }
            }
        }
        if (resFile == null) {
            // find in root directories, e.g. c:\, d:\, e:\, x:\
            File[] dirs = File.listRoots();
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    if (new File(dir, resource).isFile()) {
                        resFile = new File(dir, resource);
                    }
                }
            }
        }
        if (resFile == null) {
            // in temp directory
            final String dir = System.getProperty("java.io.tmpdir");
            if (!Helper.isEmptyOrNull(dir)) {
                if (new File(dir, resource).isFile()) {
                    resFile = new File(dir, resource);
                }
            }
        }
        if (Helper.DEBUG) {
            if (resFile != null) {
                System.out.println("找到：" + resFile.getAbsolutePath());
            } else {
                System.err.println("没有找到：" + resource);
            }
        }
        return resFile;
    }

    public static final void close(final OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // silent
            }
        }
    }

    public static final void close(final InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // silent
            }
        }
    }

    public final static boolean isEmptyOrNotExists(final String file) {
        File f = new File(file);
        return !f.isFile() || (f.length() == 0);
    }

    public static final void close(final Reader in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // silent
            }
        }
    }

    public static final void close(final Writer out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // silent
            }
        }
    }

    private static final ByteBuffer IO_BB = ByteBuffer.wrap(new byte[Helper.BUFFER_SIZE]);

    public static final long readStatsFile(final String file) {
        try {
            ByteBuffer bb = Helper.readBytes(file);
            return Long.parseLong(ArrayHelper.toString(bb));
        } catch (Exception e) {
            return 0L;
        }
    }

    public static final void writeStatsFile(final String file, final long status) {
        try {
            Helper.writeBytes(String.valueOf(status).getBytes(Helper.CHARSET_UTF8), file);
        } catch (Exception e) {
            System.err.println("写出状态文件'" + file + "'时出错：" + e.toString());
        }
    }

    public static final void appendCookies(final StringBuffer cookie, final HttpURLConnection conn) {
        List<String> values = conn.getHeaderFields().get("Set-Cookie");
        if (values != null) {
            for (String v : values) {
                if (v.indexOf("deleted") == -1) {
                    if (cookie.length() > 0) {
                        cookie.append("; ");
                    }
                    cookie.append(v.split(";")[0]);
                }
            }
        }
    }

    public static String unescapeCode(String str) throws IOException {
        StringBuffer sb = new StringBuffer(str.length());
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() == 4) {
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        sb.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        // ignore
                    }
                }
                continue;
            }
            if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        sb.append('\\');
                        break;
                    case '\'':
                        sb.append('\'');
                        break;
                    case '\"':
                        sb.append('"');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'u': {
                        inUnicode = true;
                        break;
                    }
                    default:
                        sb.append(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            sb.append(ch);
        }
        if (hadSlash) {
            sb.append('\\');
        }
        return sb.toString();
    }

    public final static void traverseDirAction(final File directory, final FileFilter filter, final FileFilter action)
            throws IOException {
        if (directory.isDirectory()) {
            final File[] childs = directory.listFiles();
            boolean found = false;
            for (File child : childs) {
                if (child.isDirectory()) {
                    Helper.traverseDirAction(child, filter, action);
                } else if (!found && filter.accept(child)) {
                    found = true;
                }
            }
            if (found) {
                action.accept(directory);
            }
        }
    }

    public final static void traverseFileAction(final File directory, final FileFilter action) throws IOException {
        if (directory.isDirectory()) {
            final File[] childs = directory.listFiles();
            for (File child : childs) {
                if (child.isDirectory()) {
                    Helper.traverseFileAction(child, action);
                } else {
                    action.accept(child);
                }
            }
        }
    }

    public static String[] parseLanguages(File file) {
        String base = file.getName().substring(0, file.getName().indexOf('.'));
        int idx2 = base.lastIndexOf('_');
        int idx1 = base.lastIndexOf('_', idx2 - 1);
        String lng1 = base.substring(idx1 + 1, idx2);
        String lng2 = base.substring(idx2 + 1);
        return new String[] {
                lng1, lng2
        };
    }

    public final static String cut(String text, int max) {
        if (text.length() < max) {
            return text;
        } else {
            text = text.substring(0, max) + "...";
            return text;
        }
    }

}

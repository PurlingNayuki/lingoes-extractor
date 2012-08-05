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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import cn.kk.extractor.lingoes.ArrayHelper.SensitiveStringDecoder;

// https://skydrive.live.com/?cid=A10100D37ADC7AD3&sc=documents
public class LingoesLd2Extractor {
    private boolean started = false;
    private long startedTime;
    private static final ArrayHelper.SensitiveStringDecoder[] AVAIL_ENCODINGS = {
            new ArrayHelper.SensitiveStringDecoder(Charset.forName("UTF-8")),
            new ArrayHelper.SensitiveStringDecoder(Charset.forName("UTF-16LE")),
            new ArrayHelper.SensitiveStringDecoder(Charset.forName("UTF-16BE")),
            new ArrayHelper.SensitiveStringDecoder(Charset.forName("EUC-JP"))
    };

    private static final byte[] TRANSFER_BYTES = new byte[Helper.BUFFER_SIZE];
    private final Main main;

    public LingoesLd2Extractor(Main main) {
        this.main = main;
    }

    public int extractLd2ToFile(File ld2File, File outFile) throws IOException {
        this.started = true;
        this.startedTime = System.currentTimeMillis();
        int counter = 0;

        // read lingoes ld2 into byte array
        FileChannel fChannel = new RandomAccessFile(ld2File, "r").getChannel();
        ByteBuffer dataRawBytes = null;
        try {
            dataRawBytes = ByteBuffer.allocate((int) fChannel.size());
            fChannel.read(dataRawBytes);
        } finally {
            fChannel.close();
        }
        if (dataRawBytes != null) {
            dataRawBytes.order(ByteOrder.LITTLE_ENDIAN);
            dataRawBytes.rewind();

            int offsetData = dataRawBytes.getInt(0x5C) + 0x60;
            if (dataRawBytes.limit() > offsetData) {
                int type = dataRawBytes.getInt(offsetData);
                int offsetWithInfo = dataRawBytes.getInt(offsetData + 4) + offsetData + 12;
                if (type == 3) {
                    counter = readDictionary(ld2File, outFile, dataRawBytes, offsetData);
                } else if (dataRawBytes.limit() > (offsetWithInfo + 0x1C)) {
                    counter = readDictionary(ld2File, outFile, dataRawBytes, offsetWithInfo);
                } else {
                    System.err.println("文件不包含字典数据。网上字典？");
                }
            } else {
                System.err.println("文件不包含字典数据。网上字典？");
            }

            this.main.setStatusDirect(counter, counter,
                    LingoesLd2Extractor.calcSpeed(counter, this.startedTime, System.currentTimeMillis()));
            return counter;
        } else {
            throw new RuntimeException("文件不包含字典数据。网上字典？");
        }
    }

    private int readDictionary(File ld2File, File outputFile, ByteBuffer dataRawBytes, int offsetData)
            throws IOException, FileNotFoundException, UnsupportedEncodingException {
        int counter;
        int limit = dataRawBytes.getInt(offsetData + 4) + offsetData + 8;
        int offsetIndex = offsetData + 0x1C;
        int offsetCompressedDataHeader = dataRawBytes.getInt(offsetData + 8) + offsetIndex;
        int inflatedWordsIndexLength = dataRawBytes.getInt(offsetData + 12);
        int inflatedWordsLength = dataRawBytes.getInt(offsetData + 16);
        List<Integer> deflateStreams = new ArrayList<Integer>();
        dataRawBytes.position(offsetCompressedDataHeader + 8);
        int offset = dataRawBytes.getInt();
        while (this.started && ((offset + dataRawBytes.position()) < limit)) {
            offset = dataRawBytes.getInt();
            deflateStreams.add(Integer.valueOf(offset));
        }
        ByteBuffer inflatedBytes = LingoesLd2Extractor.inflate(dataRawBytes, deflateStreams);

        counter = extract(inflatedBytes, inflatedWordsIndexLength, inflatedWordsIndexLength + inflatedWordsLength,
                outputFile);
        return counter;
    }

    private int extract(ByteBuffer inflatedBytes, int offsetDefs, int offsetXml, File outputFile) throws IOException,
            FileNotFoundException, UnsupportedEncodingException {

        int counter = 0;
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile), Helper.BUFFER_SIZE);
        try {
            inflatedBytes.order(ByteOrder.LITTLE_ENDIAN);

            final int dataLen = 10;
            final int defTotal = (offsetDefs / dataLen) - 1;

            this.main.setStatus(defTotal, 0, 0);
            int[] idxData = new int[6];
            String[] defData = new String[2];

            final ArrayHelper.SensitiveStringDecoder[] encodings = detectEncodings(inflatedBytes, offsetDefs,
                    offsetXml, defTotal, dataLen, idxData, defData);

            inflatedBytes.position(8);
            int failCounter = 0;
            for (int i = 0; i < defTotal; i++) {
                readDefinitionData(inflatedBytes, offsetDefs, offsetXml, dataLen, encodings[0], encodings[1], idxData,
                        defData, i);

                defData[0] = defData[0].trim();
                defData[1] = defData[1].trim();

                if (defData[0].isEmpty() || defData[1].isEmpty()) {
                    failCounter++;
                }
                if (failCounter > (defTotal * 0.01)) {
                    System.err.println("??");
                    System.err.println(defData[0] + " = " + defData[1]);
                }
                // if (lng1Chinese) {
                // defData[0] = ChineseHelper.toSimplifiedChinese(defData[0]);
                // }
                // if (lng2Chinese) {
                // defData[1] = ChineseHelper.toSimplifiedChinese(defData[1]);
                // }

                String def = defData[0] + " = " + defData[1];
                this.main.setExample(i, Helper.cut(def, 30));
                outputWriter.write(def);
                outputWriter.write("\r\n");
                counter++;
                this.main.setStatus(defTotal, i,
                        LingoesLd2Extractor.calcSpeed(i, this.startedTime, System.currentTimeMillis()));
            }
        } finally {
            outputWriter.close();
        }
        return counter;
    }

    private static final int calcSpeed(int finished, long started, long now) {
        double timeDiff = now - started;
        if (timeDiff > 0) {
            double speed = (finished * 1000.0) / timeDiff;
            return (int) speed;
        } else {
            return 0;
        }
    }

    private final ArrayHelper.SensitiveStringDecoder[] detectEncodings(final ByteBuffer inflatedBytes,
            final int offsetWords, final int offsetXml, final int defTotal, final int dataLen, final int[] idxData,
            final String[] defData) throws UnsupportedEncodingException {
        final int test = Math.min(defTotal, 500);
        Pattern p = Pattern.compile("^.*[\\x00-\\x1f].*$");
        for (SensitiveStringDecoder element : LingoesLd2Extractor.AVAIL_ENCODINGS) {
            for (SensitiveStringDecoder element2 : LingoesLd2Extractor.AVAIL_ENCODINGS) {
                try {
                    readDefinitionData(inflatedBytes, offsetWords, offsetXml, dataLen, element, element2, idxData,
                            defData, test);
                    System.out.println("词组编码：" + element.name);
                    System.out.println("XML编码：" + element2.name);
                    return new ArrayHelper.SensitiveStringDecoder[] {
                            element, element2
                    };
                } catch (Throwable e) {
                    // ignore
                }
            }
        }
        System.err.println("自动识别编码失败！选择UTF-16LE继续。");
        return new ArrayHelper.SensitiveStringDecoder[] {
                LingoesLd2Extractor.AVAIL_ENCODINGS[1], LingoesLd2Extractor.AVAIL_ENCODINGS[1]
        };
    }

    private void readDefinitionData(final ByteBuffer inflatedBytes, final int offsetWords, final int offsetXml,
            final int dataLen, final ArrayHelper.SensitiveStringDecoder wordDecoder,
            final ArrayHelper.SensitiveStringDecoder valueDecoder, final int[] wordIdxData, final String[] wordData,
            final int idx) throws UnsupportedEncodingException {
        LingoesLd2Extractor.getIdxData(inflatedBytes, dataLen * idx, wordIdxData);
        int lastWordPos = wordIdxData[0];
        int lastXmlPos = wordIdxData[1];
        int refs = wordIdxData[3];
        int currentWordOffset = wordIdxData[4];
        int currenXmlOffset = wordIdxData[5];
        String xml = LingoesLd2Extractor.strip(new String(valueDecoder.decode(inflatedBytes.array(), offsetXml
                + lastXmlPos, currenXmlOffset - lastXmlPos)));
        while (this.started && (refs-- > 0)) {
            int ref = inflatedBytes.getInt(offsetWords + lastWordPos);
            LingoesLd2Extractor.getIdxData(inflatedBytes, dataLen * ref, wordIdxData);
            lastXmlPos = wordIdxData[1];
            currenXmlOffset = wordIdxData[5];
            if (xml.isEmpty()) {
                xml = LingoesLd2Extractor.strip(new String(valueDecoder.decode(inflatedBytes.array(), offsetXml
                        + lastXmlPos, currenXmlOffset - lastXmlPos)));
            } else {
                xml = LingoesLd2Extractor.strip(new String(valueDecoder.decode(inflatedBytes.array(), offsetXml
                        + lastXmlPos, currenXmlOffset - lastXmlPos)))
                        + Helper.SEP_LIST + xml;
            }
            lastWordPos += 4;
        }
        wordData[1] = xml;

        String word = new String(wordDecoder.decode(inflatedBytes.array(), offsetWords + lastWordPos, currentWordOffset
                - lastWordPos));
        wordData[0] = word;
    }

    private static String strip(String xml) {
        int open = 0;
        int end = 0;
        if ((open = xml.indexOf("<![CDATA[")) != -1) {
            if ((end = xml.indexOf("]]>", open)) != -1) {
                return xml.substring(open + "<![CDATA[".length(), end).replace('\t', ' ')
                        .replace(Helper.SEP_NEWLINE_CHAR, ' ').replace('\u001e', ' ').replace('\u001f', ' ');
            }
        } else if ((open = xml.indexOf("<Ô")) != -1) {
            if ((end = xml.indexOf("</Ô", open)) != -1) {
                open = xml.indexOf(">", open + 1);
                return xml.substring(open + 1, end).replace('\t', ' ').replace(Helper.SEP_NEWLINE_CHAR, ' ')
                        .replace('\u001e', ' ').replace('\u001f', ' ');
            }
        } else {
            StringBuilder sb = new StringBuilder();
            end = 0;
            open = xml.indexOf('<');
            do {
                if ((open - end) > 1) {
                    sb.append(xml.substring(end + 1, open));
                }
                open = xml.indexOf('<', open + 1);
                end = xml.indexOf('>', end + 1);
            } while ((open != -1) && (end != -1));
            return sb.toString().replace('\t', ' ').replace(Helper.SEP_NEWLINE_CHAR, ' ').replace('\u001e', ' ')
                    .replace('\u001f', ' ');
        }
        return Helper.EMPTY_STRING;
    }

    private static final void getIdxData(final ByteBuffer dataRawBytes, final int position, final int[] wordIdxData) {
        dataRawBytes.position(position);
        wordIdxData[0] = dataRawBytes.getInt();
        wordIdxData[1] = dataRawBytes.getInt();
        wordIdxData[2] = dataRawBytes.get() & 0xff;
        wordIdxData[3] = dataRawBytes.get() & 0xff;
        wordIdxData[4] = dataRawBytes.getInt();
        wordIdxData[5] = dataRawBytes.getInt();
    }

    private static final ByteBuffer inflate(final ByteBuffer dataRawBytes, final List<Integer> deflateStreams)
            throws IOException {
        int startOffset = dataRawBytes.position();
        int offset = -1;
        int lastOffset = startOffset;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Integer offsetRelative : deflateStreams) {
            offset = startOffset + offsetRelative.intValue();
            LingoesLd2Extractor.decompress(out, dataRawBytes, lastOffset, offset - lastOffset);
            lastOffset = offset;
        }
        return ByteBuffer.wrap(out.toByteArray());
    }

    private static final long decompress(final ByteArrayOutputStream out, final ByteBuffer data, final int offset,
            final int length) throws IOException {
        Inflater inflator = new Inflater();
        InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(data.array(), offset, length),
                inflator, Helper.BUFFER_SIZE);
        long bytesRead = -1;
        try {
            LingoesLd2Extractor.writeInputStream(in, out);
            bytesRead = inflator.getBytesRead();
            inflator.end();
        } finally {
            in.close();
        }
        return bytesRead;
    }

    private static final void writeInputStream(final InputStream in, final OutputStream out) throws IOException {
        int len;
        while ((len = in.read(LingoesLd2Extractor.TRANSFER_BYTES)) > 0) {
            out.write(LingoesLd2Extractor.TRANSFER_BYTES, 0, len);
        }
    }

    public void cancel() {
        this.started = false;
    }

}

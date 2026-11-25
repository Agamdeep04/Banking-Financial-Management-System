
package csv;

import java.io.*;

public class FileManager {

    public void writeTextFile(String path, String content)
            throws IOException {

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        }
    }

    public String readTextFile(String path) throws IOException {

        StringBuilder result = new StringBuilder();

        try (FileReader reader = new FileReader(path)) {

            int ch;

            while ((ch = reader.read()) != -1) {
                result.append((char) ch);
            }
        }

        return result.toString();
    }

    public void writeBinaryFile(String path, byte[] data)
            throws IOException {

        try (FileOutputStream output = new FileOutputStream(path)) {
            output.write(data);
        }
    }

    public byte[] readBinaryFile(String path) throws IOException {

        try (FileInputStream input = new FileInputStream(path)) {
            return input.readAllBytes();
        }
    }

    public void tokenizeCsvFile(String path) throws IOException {

        try (FileReader reader = new FileReader(path)) {

            StreamTokenizer tokenizer = new StreamTokenizer(reader);

            tokenizer.resetSyntax();

            tokenizer.wordChars('a', 'z');
            tokenizer.wordChars('A', 'Z');
            tokenizer.wordChars('0', '9');
            tokenizer.wordChars('_', '_');
            tokenizer.wordChars(' ', ' ');

            tokenizer.ordinaryChar(',');
            tokenizer.quoteChar('"');
            tokenizer.whitespaceChars('\n', '\n');
            tokenizer.whitespaceChars('\r', '\r');

            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {

                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    System.out.println("WORD: " + tokenizer.sval);
                }

                else if (tokenizer.ttype == '"') {
                    System.out.println("QUOTED: " + tokenizer.sval);
                }

                else if (tokenizer.ttype == ',') {
                    System.out.println("COMMA");
                }
            }
        }
    }
}


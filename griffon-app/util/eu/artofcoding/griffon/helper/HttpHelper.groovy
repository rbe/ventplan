package eu.artofcoding.griffon.helper

public class HttpHelper {

    public static String download(String url) {
        OutputStream stream = new ByteArrayOutputStream()
        BufferedOutputStream out = new BufferedOutputStream(stream)
        out << new URL(url).openStream()
        out.close()
        return new String(stream.toByteArray())
    }

}

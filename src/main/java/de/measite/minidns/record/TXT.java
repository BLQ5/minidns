package de.measite.minidns.record;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.measite.minidns.Record.TYPE;

/**
 * TXT record (actually a binary blob containing extents, each of which is a one-byte count
 *  followed by that many bytes of data, which can usually be interpreted as ASCII strings
 *  but not always
 */
public class TXT implements Data {

    protected final byte[] blob;

    public TXT(DataInputStream dis, byte[] data, int length) throws IOException {
        blob = new byte[length];
        dis.readFully(blob);
    }

    public byte[] getBlob() {
        return blob;
    }

    public String getText() {
        List<byte[]> extents = getExtents();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < extents.size() - 1) {
            sb.append(new String(extents.get(i))).append(" / ");
            i++;
        }
        sb.append(new String(extents.get(i)));
        return sb.toString();
    }

    public List<byte[]> getExtents() {
        ArrayList<byte[]> extents = new ArrayList<byte[]>();
        int used = 0;
        while (used < blob.length) {
            int segLength = 0x00ff & blob[used];
            int end = ++used + segLength;
            byte[] extent = Arrays.copyOfRange(blob, used, end);
            extents.add(extent);
            used += segLength;
        }
        return extents;
    }

    @Override
    public byte[] toByteArray() {
        return blob;
    }

    @Override
    public TYPE getType() {
        return TYPE.TXT;
    }

    @Override
    public String toString() {
        return "\"" + getText() + "\"";
    }

}

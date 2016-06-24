package com.mapopolis.viewer.core;

import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.route.*;
import com.mapopolis.viewer.engine.*;

import java.io.*;
import java.util.*;

/**
 * Represents each map file, with specific info about each, also holds Vector of
 * all map files.
 */
public class MapFile {
    //public static final int LevelOffset = 2;
    //public static final int FeatureTypeOffset = 11;

    static boolean newFormat = true;
    static int boxLength = 0; // 8
    static int divLength = 0; // 2
    static boolean StringsCompressed = true;

    static {
        if (newFormat) {
            boxLength = 8;
            divLength = 2;
            StringsCompressed = false;
        }
    }
    public static final int FeatureStartToPointsStart = 4 + boxLength + divLength + 2;
    public static int HeaderLen = 4 + boxLength + divLength;
    public static final int LevelOffset = 2 + boxLength;
    public static final int FeatureTypeOffset = 3 + boxLength;
    private static final int LengthOfRecordSummary = 9; // navcard=9 standard =12
    //---------------------------------------------------------------------------
    private static final int LengthOfPalmHeader = 78;
    private static final int LengthOfMapopolisMapHeader = 440;
    private static final int CityChunkCode = 116;
    private static final int MaxOpenFiles = 300;
    private static PrintWriter out = null;
    // instance
    private int sig;
    private String filename;
    private RandomAccessFile inFile = null;
    private int fileLength;
    private CostArray costArray;
    private int nRecords;
    private MapFeatureRecord[] mapFeatureRecords;
    private int maxSequenceID;
    private String friendlyName;
    private String pathName;
    private Box boundingBox = null;
    private boolean stringsCompressed;
    // public
    public int myIndex;
    public int X0;
    public int Y0;
    public int X1;
    public int Y1;
    public int dataRecords;
    public Vector<SearchArea> cities = null;

    public MapFile(String stMapFileName, int iMapFileIndex) throws MapopolisException {
        try {
            friendlyName = filename = stMapFileName;

            RandomAccessFile in = new RandomAccessFile(stMapFileName, "r");
            initialize(in);

            in.close();

            //Engine.out(friendlyName() + " " + maxSequenceID + " " + boundingBox + " " + stringsCompressed());

            myIndex = iMapFileIndex;
        } catch (IOException e) {
            Engine.out(e.toString());
            e.printStackTrace();
            throw new MapopolisException(e.toString());
        }
    }

    RandomAccessFile getInputStream() throws MapopolisException {
        if (inFile != null) {
            return inFile;
        }

        try {
            /*
            if ( openFiles.size() > MaxOpenFiles )
            {
            ////////////////////
            // for now close all
            ////////////////////

            for ( int i = 0; i < openFiles.size(); ++i )
            {
            MapFile m = (MapFile) openFiles.elementAt(i);
            m.inFile.close();
            m.inFile = null;
            }

            openFiles = new Vector();
            }
             */

            inFile = new RandomAccessFile(filename, "r");
            //openFiles.addElement(this);
        } catch (IOException e) {
            Engine.out(e.toString());
            e.printStackTrace();
            throw new MapopolisException(e.toString());
        }

        return inFile;
    }

    public String friendlyName() {
        return friendlyName;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Receives a Vector of map names as Strings and initializes the maps
     * 
     * @param v
     *            the map names as a Vector
     *  
     */
    /**
     * Returns a String describing the map with specified name
     */
    public String getMapInfo() {
        return "not implemented";
    }

    /**
     * Returns the type of map which may be Navigator, Platinum, Demo, etc.
     */
    public int getMapType() {
        return 0;
    }

    public int getMapHeight() {
        return (Y1 - Y0);
    }

    public int getMapWidth() {
        return (X1 - X0);
    }

    public CostArray getCostArray() {
        return costArray;
    }

    public MapFeatureRecord[] getMapFeatureRecords() {
        return mapFeatureRecords;
    }

    public Vector<SearchArea> getCities() throws MapopolisException {
        if (cities != null) {
            return cities;
        }

        Vector<SearchArea> vecCityResults = new Vector<SearchArea>();
        int  f = 0;

        for (int i = dataRecords - 1; i >= 0; --i) {
            MapFeatureRecord rec = getMapFeatureRecords()[i];

            if (rec.isFeatureRecord) {
                break;
            } else {
                f = i;
            }
        }

        for (int i = f; i < dataRecords; ++i) {
            MapFeatureRecord rec = getMapFeatureRecords()[i];

            if (rec.isFeatureRecord) {
                break;
            }

            if (rec.getChunkType() == CityChunkCode) {
                byte[] buf = rec.getAsBytes();
                int off = 1;

                int n = IO.get2(buf, off);
                off += 2;

                //Engine.out("Get Cities MapFile chunk " + n + " " + friendlyName());

                for (int j = 0; j < n; ++j) {
                    SN sn = Utilities.expandString(buf, off, stringsCompressed());

                    off += sn.bytes;

                    // get bounding box

                    Box b = getCompressedBox(buf, off);
                    off += 8;

                    //Engine.out(j + " " + sn.name + ", " + b);

                    /*
                    int k = 0;

                    while ( buf[off + k] != 0 ) k++;

                    String name = new String(buf, off, k);

                    Engine.out(name);

                    if ( name.charAt(0) == '#' )
                    name = "POSTAL CODE " + name.substring(2, name.length());

                    name += ", " + regionName;
                     */



                    //off += k + 1;

                    vecCityResults.addElement(new SearchArea(sn.name, b));

                    //Engine.out("===" + sn.name + "===");
                    //if ( false )
                    //{
                    //addToSearchAreaResults(results, sn.name, index);
                    //index++;
                    //}
                }
            }
        }

        cities = vecCityResults;

        return vecCityResults;
    }

    public byte[] statesInMapEntry() {
        Vector<String> vecStates = new Vector<String>();

        String line = "";
        String name = "Map-" + this.friendlyName;

        for (int i = 0; i < cities.size(); ++i) {
            SearchArea sa = (SearchArea) cities.elementAt(i);

            if (!sa.name.startsWith("#")) {
                if (sa.name.charAt(sa.name.length() - 3) != ' ') {
                    //Engine.out("no state " + sa.name);
                } else {
                    String stState = sa.name.substring(sa.name.length() - 2);
                    boolean fd = false;
                    for (int j = 0; j < vecStates.size(); ++j) {
                        if (((String) vecStates.elementAt(j)).equals(stState)) {
                            fd = true;
                            break;
                        }
                    }
                    if (!fd) {
                        line += stState + " ";
                        vecStates.add(stState);
                    }
                }
            }
        }

        byte[] bytes = new byte[100];
        char[] bn = name.toCharArray();
        for (int i = 0; i < bn.length; ++i) {
            bytes[i] = (byte) bn[i];
        }
        bn = line.toCharArray();
        for (int i = 0; i < bn.length; ++i) {
            if (bn[i] == ' ') {
                bytes[i + 50] = 0;
            } else {
                bytes[i + 50] = (byte) bn[i];
            }
        }

        //Engine.out(name + " " + line + " " + myIndex);

        return bytes;
    }

    @Override
    public boolean equals(Object obj) {
        return filename.equals(((MapFile) obj).filename);
    }

    @Override
    public String toString() {
        return filename;
    }

    //void displayRecords()
    //	
    //{
    //	for ( int i = 0; i < nRecords - 2; ++i )
    //	{
    //		MapFeatureRecord m = mapFeatureRecords[i];
    //		System.out.println(i + " " + m.type + " " + m.isFeatureRecord + " " +
    // m.featureCount + " " + m.X0 + " " + m.Y0 + " " + m.X1 + " " + m.Y1 + " "
    // + m.length);
    //	}
    //}

    /*
    int getZipIndexx(int z)

    {
    String target = "" + (z + 100000);
    target = "# " + target.substring(1);

    //Engine.out("target=" + target);

    for ( int i = 0; i < cities.size(); ++i )
    {
    SearchArea s = (SearchArea) cities.elementAt(i);

    //Engine.out(s.name);

    if ( s.name.equals(target) )
    return i;
    }

    return -1;
    }
     */
    public static Vector<MapFile> initAllMaps(Vector<String> vecMapName) throws IOException, MapopolisException {
        Vector<MapFile> vecMapFileObj = new Vector<MapFile>();

        for (int i = 0; i < vecMapName.size(); ++i) {
            vecMapFileObj.addElement(new MapFile((String) vecMapName.elementAt(i), vecMapFileObj.size()));
        }

        return vecMapFileObj;
    }

    private boolean initialize(RandomAccessFile inFile) throws IOException, MapopolisException {
        byte[] buffer;

        RandomAccessFile in = getInputStream();

        in = inFile;
        fileLength = (int) in.length();

        in.seek(0);

        buffer = new byte[LengthOfPalmHeader];

        if (in.read(buffer) != LengthOfPalmHeader) {
            throw new MapopolisException("Invalid Map File");
        }

        // check the palm header record fields - the header is the first 78
        // bytes

        // read the pointers to each record

        nRecords = IO.get2(buffer, LengthOfPalmHeader - 2);
        dataRecords = nRecords - 2;

        //Engine.out("N records = " + nRecords + " length = " + fileLength);

        // the first record is the header record
        // followed by the summary record
        // followed by mapFeatureRecords then
        // other records. There may be less than
        // nRecords - 2.

        mapFeatureRecords = new MapFeatureRecord[dataRecords];

        int dataOffset = 0, off, frecord = 0;

        //byte[] originalName = new byte[33];
        //for ( int i = 0; i < 32; ++i )
        //	originalName[i] = buffer[i];
        //originalName[32] = 0;
        //mapname = new String(originalName);
        //for ( int i = 0; i < mapname.length(); ++i )
        //	if ( mapname.charAt(i) < ' ' )
        //	{
        //		mapname = mapname.substring(0, i);
        //		break;
        //	}
        // now read the locations of each record
//read offset record
        buffer = new byte[nRecords * 8 + 2];

        if (in.read(buffer) != nRecords * 8 + 2) {
            throw new MapopolisException("Invalid Record Offset Table  ");
        }

        int mapHeaderRecordOffset = 0, mapChunkSummaryTableOffset = 0, lastOff = 0;

        for (int i = 0; i < nRecords; ++i) {
            off = IO.get4(buffer, 8 * i);

            if (i == 0) {
                mapHeaderRecordOffset = off;
            } else if (i == 1) {
                mapChunkSummaryTableOffset = off;
            } else {
                mapFeatureRecords[i - 2] = new MapFeatureRecord(this, off);

                if (i > 2) {
                    mapFeatureRecords[i - 3].length = off - lastOff;
                }
            }

            lastOff = off;
        }

        mapFeatureRecords[nRecords - 3].length = fileLength - lastOff;

        // now read the map header record

        in.seek(mapHeaderRecordOffset);

        buffer = new byte[LengthOfMapopolisMapHeader];

        if (in.read(buffer) != LengthOfMapopolisMapHeader) {
            throw new MapopolisException("Invalid Map File");
        }

        readMapHeader(buffer);


        Engine.out(friendlyName + " " + (boundingBox.x1 - boundingBox.x0) + " " + (boundingBox.y1 - boundingBox.y0) + " " + boundingBox);


        // now read the summary record - the count is in the first 2 bytes

        buffer = new byte[2];

        if (in.read(buffer) != 2) {
            throw new MapopolisException("Invalid Map File");
        }

        int nchunks = IO.get2(buffer, 0);

        //Engine.out(this.friendlyName + " nrecords=" + nRecords + " nchunks=" + nchunks + " diff=" + (nRecords - nchunks));

        buffer = new byte[LengthOfRecordSummary];

        for (int i = 0; i < nchunks; ++i) {
            if (in.read(buffer) != LengthOfRecordSummary) {
                throw new MapopolisException("Invalid Map File");
            }

            int type = IO.get1(buffer, 0);
            int k = 1;

            Box bbox = getCompressedBox(buffer, k);

            if (type < 0 || type > 6) {
                Engine.out("Chunk " + i + " type = " + type + ", " + bbox);
            }

            mapFeatureRecords[i].initializeAsFeatureRecord(type, bbox);
        }

        for (int i = 0; i < mapFeatureRecords.length; ++i) {
            mapFeatureRecords[i].myIndex = i;
        }


        //for (int i = 0; i < nchunks; ++i)
        //{
        //	//if (mapFeatureRecords[i].getChunkType() < 3)
        //	{
        //		//Engine.out("initialize " + i);
        //		mapFeatureRecords[i].test();
        //	}
        //}


        getCities();

        //Engine.out("N cities=" + cities.size());

        // the other records are always at the end of the file so
        // we can read backwards in mapFeatureRecords from nRecords - 3 to
        // access them

        return true;
    }

    private void readMapHeader(byte[] buffer) {
        sig = IO.get4(buffer, 0);

        X0 = IO.get4(buffer, 6);
        Y0 = IO.get4(buffer, 10);
        X1 = IO.get4(buffer, 14);
        Y1 = IO.get4(buffer, 18);

        boundingBox = new Box(X0, Y0, X1, Y1);

        // set all stuff

        int k = 30;

        while (buffer[k] != 0) {
            k++;
        }

        friendlyName = new String(buffer, 30, k - 30);
        friendlyName = Utilities.replace(friendlyName, "_", ", ");


        stringsCompressed = (IO.get1(buffer, 116) == 0 ? false : true);

        stringsCompressed = StringsCompressed;


        maxSequenceID = IO.get4(buffer, 120);
        costArray = new CostArray(maxSequenceID);
    }

    private Box getCompressedBox(byte[] buffer, int offset) {
        int x0 = (IO.get2(buffer, offset + 0) << 8) + X0;
        int y0 = (IO.get2(buffer, offset + 2) << 8) + Y0;
        int x1 = (IO.get2(buffer, offset + 4) << 8) + X0;
        int y1 = (IO.get2(buffer, offset + 6) << 8) + Y0;

        return new Box(x0, y0, x1, y1);
    }

    public int getAddressForUID(byte[] b) throws MapopolisException {
        for (int i = dataRecords - 1; i >= 0; --i) {
            MapFeatureRecord rec = (getMapFeatureRecords())[i];

            //if ( rec.isFeatureRecord )
            //	break;

            if (rec.getChunkType() == FeaturePoint.BoundaryNodeAssociationCode) {
                byte[] buf = rec.getAsBytes();
                int off = 1;

                int n = IO.get2(buf, off);
                off += 2;

                for (int j = 0; j < n; ++j) {
                    boolean ok = true;

                    for (int r = 0; r < 8; ++r) {
                        if (buf[off + 4 + r] != b[r]) {
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        return IO.get4(buf, off);
                    }

                    off += 12;
                }
            }
        }

        return 0;
    }

    public Box boundingBox() {
        return boundingBox;
    }

    public void test() throws MapopolisException {
        for (int i = 0; i < getMapFeatureRecords().length; ++i) {
            MapFeatureRecord mfr = getMapFeatureRecords()[i];
            if (mfr.isFeatureRecord) {
                int[] ind = mfr.getFeatureIndices();

                for (int j = 1; j <= ind[0]; ++j) {
                    MapFeature mm = new MapFeature(mfr, ind[j]);
                    if (!mm.isLandmark()) {
                        continue;
                    }
                    String s = mm.getName();

                    int kk = s.indexOf("|");
                    if (kk > 0) {
                        s = s.substring(0, kk);
                        boolean found = false;

                        for (int k = 0; k < Engine.allCategories.size(); ++k) {
                            if (s.equals((String) Engine.allCategories.elementAt(k))) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            Engine.allCategories.add(s);
                        }
                    }
                }
            }
        }
    }

    public boolean stringsCompressed() {
        return stringsCompressed;
    }
}

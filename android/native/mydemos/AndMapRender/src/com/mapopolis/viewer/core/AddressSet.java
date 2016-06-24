package com.mapopolis.viewer.core;

class AddressSet

{
    int[] fromLeft = new int[10];
    int[] fromRight = new int[10];
    int[] toLeft = new int[10];
    int[] toRight = new int[10];

	
    @Override
    public String toString()

    {
        String r = "";
        for (int i = 0; i < 1; ++i) {
            r += " " +fromLeft[i] + " "
                    + fromRight[i] + " " + toLeft[i]
                    + " " + toRight[i];
        }

        return r;
    }
	
}

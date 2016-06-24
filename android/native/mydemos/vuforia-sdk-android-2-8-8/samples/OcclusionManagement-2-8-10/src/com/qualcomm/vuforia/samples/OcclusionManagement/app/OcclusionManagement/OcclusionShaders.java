/*==============================================================================
 Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.qualcomm.vuforia.samples.OcclusionManagement.app.OcclusionManagement;

public class OcclusionShaders 
{
	
	public static final String OCCLUSION_FRAGMENT_SHADER = " \n" +
		"\n" +
		"precision mediump float;\n" +
		"varying vec2 texCoord;\n" +
		"uniform sampler2D texSamplerMask;\n" +
		"uniform sampler2D texSamplerVideo;\n" +
		"uniform vec2 viewportOrigin;\n" +
		"uniform vec2 viewportSize;\n" +
		"uniform vec2 textureRatio;\n" +
		"uniform vec2 prefix;\n" +
		"uniform vec2 inversion_multiplier;\n" +
		"uniform bool portrait;\n" +
		"\n"+
		"void main() \n"+
		"{ \n"+
			"vec2 screenCoord;\n" +
			"float normalized_coordinates[2];\n" +
			"\n"+
			"// The following equations calculate the appropriate UV coordinates\n" +
			"// to take from the video sampler. They consider whether the screen\n" +
			"// is in landscape or portrait mode and whether it uses the front (reflected)\n" +
			"// or back camera. The actual coefficients are passed by the main app.\n" +
			"//\n" +
			"\n"+
			"normalized_coordinates[0] = (gl_FragCoord.x-viewportOrigin.x)/viewportSize.x;\n" +
			"normalized_coordinates[1] = (gl_FragCoord.y-viewportOrigin.y)/viewportSize.y;\n" +
            "\n"+
			"if (!gl_FrontFacing)\n"+
            "{ \n"+
            "    discard;\n"+
            "} \n" +
            "\n"+
			"if (portrait)\n" +
			"{ \n"+
			"    screenCoord.x = (prefix.x + (inversion_multiplier.x * normalized_coordinates[1])) * textureRatio.x;\n" +
			"    screenCoord.y = (prefix.y + (inversion_multiplier.y * normalized_coordinates[0])) * textureRatio.y;\n" +
			"} \n" +
			"else\n" +
			"{ \n"+
				"screenCoord.x = (prefix.x + (inversion_multiplier.x * normalized_coordinates[0])) * textureRatio.x;\n" +
				"screenCoord.y = (prefix.y + (inversion_multiplier.y * normalized_coordinates[1])) * textureRatio.y;\n" +
			"} \n" +
			"\n" +
			"vec3 videoColor = texture2D(texSamplerVideo, screenCoord.xy).rgb;\n" +
			"float maskColor  = texture2D(texSamplerMask, texCoord.xy).x;\n" +
			"gl_FragColor.rgba = vec4(videoColor.r, videoColor.g, videoColor.b, maskColor);\n" +
			"\n"+
		"} \n";
	
}

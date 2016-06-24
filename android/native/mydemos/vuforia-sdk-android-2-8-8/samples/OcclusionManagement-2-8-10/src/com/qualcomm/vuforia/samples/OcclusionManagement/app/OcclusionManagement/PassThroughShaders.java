/*==============================================================================
 Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.qualcomm.vuforia.samples.OcclusionManagement.app.OcclusionManagement;

public class PassThroughShaders 
{
	
	public static final String PASS_THROUGH_VERTEX_SHADER = " \n" +
			  "\n" +
			  "attribute vec4 vertexPosition; \n"+
			  "attribute vec2 vertexTexCoord; \n"+
			  "uniform mat4 modelViewProjectionMatrix; \n"+
			  "varying vec2 texCoord; \n"+
			  "\n"+
			  "void main() \n"+
			  "{ \n"+
			  "   gl_Position = modelViewProjectionMatrix * vertexPosition; \n"+
			  "   texCoord = vertexTexCoord; \n"+
			  "} \n";
	
	public static final String PASS_THROUGH_FRAGMENT_SHADER = " \n" +
			 "\n" +
			 "precision mediump float; \n" +
			 "varying vec2 texCoord; \n" +
			 "uniform sampler2D texSamplerVideo; \n" +
			 " \n" +
			 "void main() \n" +
			 "{ \n" +
			 "   gl_FragColor = texture2D(texSamplerVideo, texCoord); \n" +
			 "} \n";

}

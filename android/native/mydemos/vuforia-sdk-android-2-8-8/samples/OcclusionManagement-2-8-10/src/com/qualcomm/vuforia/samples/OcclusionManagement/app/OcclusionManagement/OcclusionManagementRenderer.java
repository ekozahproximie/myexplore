/*==============================================================================
 Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.qualcomm.vuforia.samples.OcclusionManagement.app.OcclusionManagement;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.MultiTargetResult;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoBackgroundTextureInfo;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeObject;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Teapot;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;


// The renderer class for the OcclusionManagement sample.
public class OcclusionManagementRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "OcclusionManagementRenderer";
    
    SampleApplicationSession vuforiaAppSession;
    private OcclusionManagement mActivity;
    boolean mIsActive = false;
    
    private Vector<Texture> mTextures;
    
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    
    // These values will hold the GL viewport
    int viewportPosition_x = 0;
    int viewportPosition_y = 0;
    int viewportSize_x = 0;
    int viewportSize_y = 0;
    
    double vbOrthoQuadVerticesArray[] = { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
            0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f };
    
    double vbOrthoQuadTexCoordsArray[] = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 0.0f };
    
    short vbOrthoQuadIndicesArray[] = { 0, 1, 2, 2, 3, 0 };
    
    Buffer vbOrthoQuadVertices, vbOrthoQuadTexCoords, vbOrthoQuadIndices;
    
    private CubeObject cubeObject = new CubeObject();
    private Teapot teapot = new Teapot();
    float vbOrthoProjMatrix[] = new float[16];
    
    private int vbShaderProgramOcclusionID = 0;
    private int vbVertexPositionOcclusionHandle = 0;
    private int vbVertexTexCoordOcclusionHandle = 0;
    private int vbTexSamplerVideoOcclusionHandle = 0;
    private int vbProjectionMatrixOcclusionHandle = 0;
    private int vbTexSamplerMaskOcclusionHandle = 0;
    private int vbViewportOriginHandle = 0;
    private int vbViewportSizeHandle = 0;
    private int vbTextureRatioHandle = 0;
    private int vbPrefixHandle = 0;
    private int vbInversionMultiplierHandle = 0;
    private int vbPortraitHandle = 0;
    
    private int vbShaderProgramID = 0;
    private int vbVertexPositionHandle = 0;
    private int vbVertexTexCoordHandle = 0;
    private int vbTexSamplerVideoHandle = 0;
    private int vbProjectionMatrixHandle = 0;
    
    // Constants:
    final static float kCubeScaleX = 120.0f * 0.75f / 2.0f;
    final static float kCubeScaleY = 120.0f * 1.00f / 2.0f;
    final static float kCubeScaleZ = 120.0f * 0.50f / 2.0f;
    
    final static float kTeapotScaleX = 120.0f * 0.015f;
    final static float kTeapotScaleY = 120.0f * 0.015f;
    final static float kTeapotScaleZ = 120.0f * 0.015f;
    
    
    public OcclusionManagementRenderer(OcclusionManagement activity,
        SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
    }
    
    
    // Called when the surface is created or recreated.
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        
        // Call function to initialize rendering:
        initRendering();
        
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        Vuforia.onSurfaceCreated();
    }
    
    
    // Called when the surface changed size.
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
        
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
        
        setViewport();
    }
    
    
    // Called to draw the current frame.
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content
        renderFrame();
    }
    
    
    void initRendering()
    {
        Log.d(LOGTAG, "OcclusionManagementRenderer.initRendering");
        
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
            : 1.0f);
        
        // Now generate the OpenGL texture objects and add settings
        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
            CubeShaders.CUBE_MESH_VERTEX_SHADER,
            CubeShaders.CUBE_MESH_FRAGMENT_SHADER);
        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "modelViewProjectionMatrix");
        
        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(
            PassThroughShaders.PASS_THROUGH_VERTEX_SHADER,
            PassThroughShaders.PASS_THROUGH_FRAGMENT_SHADER);
        vbVertexPositionHandle = GLES20.glGetAttribLocation(vbShaderProgramID,
            "vertexPosition");
        vbVertexTexCoordHandle = GLES20.glGetAttribLocation(vbShaderProgramID,
            "vertexTexCoord");
        vbProjectionMatrixHandle = GLES20.glGetUniformLocation(
            vbShaderProgramID, "modelViewProjectionMatrix");
        vbTexSamplerVideoHandle = GLES20.glGetUniformLocation(
            vbShaderProgramID, "texSamplerVideo");
        Matrix.orthoM(vbOrthoProjMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f,
            1.0f);
        
        vbShaderProgramOcclusionID = SampleUtils.createProgramFromShaderSrc(
            PassThroughShaders.PASS_THROUGH_VERTEX_SHADER,
            OcclusionShaders.OCCLUSION_FRAGMENT_SHADER);
        vbVertexPositionOcclusionHandle = GLES20.glGetAttribLocation(
            vbShaderProgramOcclusionID, "vertexPosition");
        vbVertexTexCoordOcclusionHandle = GLES20.glGetAttribLocation(
            vbShaderProgramOcclusionID, "vertexTexCoord");
        vbProjectionMatrixOcclusionHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "modelViewProjectionMatrix");
        vbViewportOriginHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "viewportOrigin");
        vbViewportSizeHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "viewportSize");
        vbTextureRatioHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "textureRatio");
        vbTexSamplerVideoOcclusionHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "texSamplerVideo");
        vbTexSamplerMaskOcclusionHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "texSamplerMask");
        vbPrefixHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "prefix");
        vbInversionMultiplierHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "inversion_multiplier");
        vbPortraitHandle = GLES20.glGetUniformLocation(
            vbShaderProgramOcclusionID, "portrait");
        
        vbOrthoQuadVertices = fillBuffer(vbOrthoQuadVerticesArray);
        vbOrthoQuadTexCoords = fillBuffer(vbOrthoQuadTexCoordsArray);
        vbOrthoQuadIndices = fillBuffer(vbOrthoQuadIndicesArray);
        
        setViewport();
    }
    
    
    public void setViewport()
    {
        VideoBackgroundConfig config = Renderer.getInstance()
            .getVideoBackgroundConfig();
        
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        viewportPosition_x = (((int) (metrics.widthPixels - config.getSize()
            .getData()[0])) / (int) 2) + config.getPosition().getData()[0];
        viewportPosition_y = (((int) (metrics.heightPixels - config.getSize()
            .getData()[1])) / (int) 2) + config.getPosition().getData()[1];
        viewportSize_x = config.getSize().getData()[0];
        viewportSize_y = config.getSize().getData()[1];
        Log.e(LOGTAG, "x: " + viewportPosition_x + ", y: " + viewportPosition_y
            + ", Size x: " + viewportSize_x + ", Size y: " + viewportSize_y);
    }
    
    
    private Buffer fillBuffer(double[] array)
    {
        // Convert to floats because OpenGL doesn't work on doubles, and
        // manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each
                                                                     // float
                                                                     // takes 4
                                                                     // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (double d : array)
            bb.putFloat((float) d);
        bb.rewind();
        
        return bb;
        
    }
    
    
    private Buffer fillBuffer(short[] array)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(2 * array.length); // each
                                                                     // short
                                                                     // takes 2
                                                                     // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (short s : array)
            bb.putShort(s);
        bb.rewind();
        
        return bb;
        
    }
    
    
    void renderFrame()
    {
        SampleUtils.checkGLError("Check gl errors prior render Frame");
        
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        
        int reflection = Renderer.getInstance().getVideoBackgroundConfig()
            .getReflection();
        
        VideoBackgroundTextureInfo texInfo = Renderer.getInstance()
            .getVideoBackgroundTextureInfo();
        int tempImgSize[] = texInfo.getImageSize().getData();
        int tempTexSize[] = texInfo.getTextureSize().getData();
        
        float uRatio = (tempImgSize[0] / (float) tempTexSize[0]);
        float vRatio = (tempImgSize[1] / (float) tempTexSize[1]);
        
        boolean isPortrait;
        // Detect if we are in portrait mode or not
        Configuration config = mActivity.getResources().getConfiguration();
        
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            isPortrait = false;
        else
            isPortrait = true;
        
        // The following are the texture coordinates necessary for the correct
        // mapping
        // of the PORTRAIT/LANDSCAPE and Reflection ON/OFF combinations for the
        // VideoBackground as well as the coefficients for the occlusion shader
        float inversion_multiplier[] = new float[2];
        float prefix[] = new float[2];
        if (isPortrait)
        {
            if (reflection == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            {
                // Portrait Reflection On
                //
                
                // VideoBackground Texture Coordinates
                vbOrthoQuadTexCoordsArray[0] = 0;
                vbOrthoQuadTexCoordsArray[1] = vRatio;
                vbOrthoQuadTexCoordsArray[2] = 0;
                vbOrthoQuadTexCoordsArray[3] = 0;
                vbOrthoQuadTexCoordsArray[4] = uRatio;
                vbOrthoQuadTexCoordsArray[5] = 0;
                vbOrthoQuadTexCoordsArray[6] = uRatio;
                vbOrthoQuadTexCoordsArray[7] = vRatio;
                
                // Fragment shader coefficents
                prefix[0] = 0.0f;
                prefix[1] = 1.0f;
                inversion_multiplier[0] = 1.0f;
                inversion_multiplier[1] = -1.0f;
            } else
            {
                // Portrait Reflection Off
                //
                
                // VideoBackground Texture Coordinates
                vbOrthoQuadTexCoordsArray[0] = uRatio;
                vbOrthoQuadTexCoordsArray[1] = vRatio;
                vbOrthoQuadTexCoordsArray[2] = uRatio;
                vbOrthoQuadTexCoordsArray[3] = 0;
                vbOrthoQuadTexCoordsArray[4] = 0;
                vbOrthoQuadTexCoordsArray[5] = 0;
                vbOrthoQuadTexCoordsArray[6] = 0;
                vbOrthoQuadTexCoordsArray[7] = vRatio;
                
                // Fragment shader coefficents
                prefix[0] = 1.0f;
                prefix[1] = 1.0f;
                inversion_multiplier[0] = -1.0f;
                inversion_multiplier[1] = -1.0f;
            }
        } else
        {
            // If we detect a reflection, invert the x coords for the video
            // background texture
            if (reflection == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            {
                // Landscape Reflection On
                //
                
                // VideoBackground Texture Coordinates
                vbOrthoQuadTexCoordsArray[0] = uRatio;
                vbOrthoQuadTexCoordsArray[1] = vRatio;
                vbOrthoQuadTexCoordsArray[2] = 0;
                vbOrthoQuadTexCoordsArray[3] = vRatio;
                vbOrthoQuadTexCoordsArray[4] = 0;
                vbOrthoQuadTexCoordsArray[5] = 0;
                vbOrthoQuadTexCoordsArray[6] = uRatio;
                vbOrthoQuadTexCoordsArray[7] = 0;
                
                // Fragment shader coefficents
                prefix[0] = 1.0f;
                prefix[1] = 1.0f;
                inversion_multiplier[0] = -1.0f;
                inversion_multiplier[1] = -1.0f;
            } else
            {
                // Landscape Reflection Off
                //
                
                // VideoBackground Texture Coordinates
                vbOrthoQuadTexCoordsArray[0] = 0;
                vbOrthoQuadTexCoordsArray[1] = vRatio;
                vbOrthoQuadTexCoordsArray[2] = uRatio;
                vbOrthoQuadTexCoordsArray[3] = vRatio;
                vbOrthoQuadTexCoordsArray[4] = uRatio;
                vbOrthoQuadTexCoordsArray[5] = 0;
                vbOrthoQuadTexCoordsArray[6] = 0;
                vbOrthoQuadTexCoordsArray[7] = 0;
                
                // Fragment shader coefficents
                prefix[0] = 0.0f;
                prefix[1] = 1.0f;
                inversion_multiplier[0] = 1.0f;
                inversion_multiplier[1] = -1.0f;
            }
        }
        
        vbOrthoQuadTexCoords = fillBuffer(vbOrthoQuadTexCoordsArray);
        
        // //////////////////////////////////////////////////////////////////////////
        // This section renders the video background with a
        // a shader defined in PassThroughShaders.java, it doesn't apply any effect,
        // it merely renders it
        //
        int vbVideoTextureUnit = 0;
        int vbMaskTextureUnit = 1;
        Renderer.getInstance().bindVideoBackground(vbVideoTextureUnit);
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        
        // Set the viewport
        GLES20.glViewport(viewportPosition_x, viewportPosition_y,
            viewportSize_x, viewportSize_y);
        
        // Load the shader and upload the vertex/texcoord/index data
        GLES20.glUseProgram(vbShaderProgramID);
        GLES20.glVertexAttribPointer(vbVertexPositionHandle, 3,
            GLES20.GL_FLOAT, false, 0, vbOrthoQuadVertices);
        GLES20.glVertexAttribPointer(vbVertexTexCoordHandle, 2,
            GLES20.GL_FLOAT, false, 0, vbOrthoQuadTexCoords);
        GLES20.glUniform1i(vbTexSamplerVideoHandle, vbVideoTextureUnit);
        GLES20.glUniformMatrix4fv(vbProjectionMatrixHandle, 1, false,
            vbOrthoProjMatrix, 0);
        
        // Render the video background
        GLES20.glEnableVertexAttribArray(vbVertexPositionHandle);
        GLES20.glEnableVertexAttribArray(vbVertexTexCoordHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,
            vbOrthoQuadIndices);
        GLES20.glDisableVertexAttribArray(vbVertexPositionHandle);
        GLES20.glDisableVertexAttribArray(vbVertexTexCoordHandle);
        
        // Wrap up this rendering
        GLES20.glUseProgram(0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        SampleUtils.checkGLError("Rendering of the video background");
        //
        // //////////////////////////////////////////////////////////////////////////
        
        // //////////////////////////////////////////////////////////////////////////
        // These OpenGL setup calls are important for a proper blending
        //
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //
        // //////////////////////////////////////////////////////////////////////////
        
        // Did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0)
        {
            // Get the trackable:
            TrackableResult result = null;
            int numResults = state.getNumTrackableResults();
            
            // Browse results searching for the MultiTarget
            for (int j = 0; j < numResults; j++)
            {
                result = state.getTrackableResult(j);
                if (result.isOfType(MultiTargetResult.getClassType()))
                    break;
                result = null;
            }
            
            // If it was not found exit
            if (result == null)
            {
                // Clean up and leave
                GLES20.glDisable(GLES20.GL_BLEND);
                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
                
                Renderer.getInstance().end();
                return;
            }
            
            Matrix44F modelViewMatrix_Vuforia = Tool
                .convertPose2GLMatrix(result.getPose());
            float modelViewMatrix[] = modelViewMatrix_Vuforia.getData();
            float[] modelViewProjectionCube = new float[16];
            float[] modelViewProjectionTeapot = new float[16];
            
            Matrix.scaleM(modelViewMatrix, 0, kCubeScaleX, kCubeScaleY,
                kCubeScaleZ);
            Matrix.multiplyMM(modelViewProjectionCube, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            
            // //////////////////////////////////////////////////////////////////////
            // First, we render the faces that serve as a "background" to the
            // teapot
            // with a checkerboard texture. This helps the user to have a
            // visually
            // constrained space (otherwise the teapot looks floating in space)
            //
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_FRONT);
            if (reflection == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
                GLES20.glFrontFace(GLES20.GL_CW); // Front camera
            else
                GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
                
            GLES20.glUseProgram(shaderProgramID);
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                false, 0, cubeObject.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 0, cubeObject.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                GLES20.GL_FLOAT, false, 0, cubeObject.getTexCoords());
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                mTextures.get(0).mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjectionCube, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                cubeObject.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                cubeObject.getIndices());
            
            GLES20.glCullFace(GLES20.GL_BACK);
            
            SampleUtils.checkGLError("Back faces of the box");
            //
            // //////////////////////////////////////////////////////////////////////
            
            // //////////////////////////////////////////////////////////////////////
            // Then, we render the actual teapot
            //
            modelViewMatrix = modelViewMatrix_Vuforia.getData();
            Matrix.translateM(modelViewMatrix, 0, 0.0f * 120.0f,
                -0.0f * 120.0f, -0.17f * 120.0f);
            Matrix.rotateM(modelViewMatrix, 0, 90.0f, 0.0f, 0, 1);
            Matrix.scaleM(modelViewMatrix, 0, kTeapotScaleX, kTeapotScaleY,
                kTeapotScaleZ);
            Matrix.multiplyMM(modelViewProjectionTeapot, 0, vuforiaAppSession
                .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            GLES20.glUseProgram(shaderProgramID);
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                false, 0, teapot.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 0, teapot.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                GLES20.GL_FLOAT, false, 0, teapot.getTexCoords());
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                mTextures.get(1).mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjectionTeapot, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                teapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                teapot.getIndices());
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            //
            // //////////////////////////////////////////////////////////////////////
            
            // //////////////////////////////////////////////////////////////////////
            // Finally, we render the top layer based on the video image
            // this is the layer that actually gives the "transparent look"
            // notice that we use the mask.png (textures[2]->mTextureID)
            // to define how the transparency looks, you can play around with
            // that
            // texture to change the effects.
            //
            GLES20.glDepthFunc(GLES20.GL_LEQUAL);
            Renderer.getInstance().bindVideoBackground(vbVideoTextureUnit);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + vbMaskTextureUnit);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                mTextures.get(2).mTextureID[0]);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
                GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glViewport(viewportPosition_x, viewportPosition_y,
                viewportSize_x, viewportSize_y);
            
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glUseProgram(vbShaderProgramOcclusionID);
            GLES20.glVertexAttribPointer(vbVertexPositionOcclusionHandle, 3,
                GLES20.GL_FLOAT, false, 0, cubeObject.getVertices());
            GLES20.glVertexAttribPointer(vbVertexTexCoordOcclusionHandle, 2,
                GLES20.GL_FLOAT, false, 0, cubeObject.getTexCoords());
            GLES20.glEnableVertexAttribArray(vbVertexPositionOcclusionHandle);
            GLES20.glEnableVertexAttribArray(vbVertexTexCoordOcclusionHandle);
            
            GLES20.glUniform2f(vbViewportOriginHandle, viewportPosition_x,
                viewportPosition_y);
            GLES20.glUniform2f(vbViewportSizeHandle, viewportSize_x,
                viewportSize_y);
            GLES20.glUniform2f(vbTextureRatioHandle, uRatio, vRatio);
            GLES20.glUniform2f(vbPrefixHandle, prefix[0], prefix[1]);
            GLES20.glUniform2f(vbInversionMultiplierHandle,
                inversion_multiplier[0], inversion_multiplier[1]);
            GLES20.glUniform1i(vbPortraitHandle, isPortrait ? 1 : 0);
            GLES20.glUniform1i(vbTexSamplerVideoOcclusionHandle,
                vbVideoTextureUnit);
            GLES20.glUniform1i(vbTexSamplerMaskOcclusionHandle,
                vbMaskTextureUnit);
            GLES20.glUniformMatrix4fv(vbProjectionMatrixOcclusionHandle, 1,
                false, modelViewProjectionCube, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                cubeObject.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                cubeObject.getIndices());
            GLES20.glDisableVertexAttribArray(vbVertexPositionOcclusionHandle);
            GLES20.glDisableVertexAttribArray(vbVertexTexCoordOcclusionHandle);
            GLES20.glUseProgram(0);
            
            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
            
            GLES20.glDepthFunc(GLES20.GL_LESS);
            SampleUtils.checkGLError("Transparency layer");
            //
            // //////////////////////////////////////////////////////////////////////
        }
        
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        Renderer.getInstance().end();
        
    }
    
    
    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
        
    }
    
}

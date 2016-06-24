/*==============================================================================
 Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.qualcomm.QCARSamples.Dominoes;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GUIManager {

    // Custom views:
    private View overlayView;
    private ToggleButton startButton;
    private Button clearButton;
    private Button deleteButton;

    // A Handler for working with the GUI from other threads:
    static class MyActivityHandler extends Handler
    {
        private WeakReference<GUIManager> guiManager;
        private Context context;

        MyActivityHandler(GUIManager guim, Context c)
        {
            guiManager = new WeakReference<GUIManager>(guim);
            context = c;
        }

        public void handleMessage(Message msg)
        {
            Button deleteButton = guiManager.get().deleteButton;
            ToggleButton startButton = guiManager.get().startButton;

            switch (msg.what)
            {
                case SHOW_DELETE_BUTTON:
                    if (deleteButton != null)
                    {
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                    break;

                case HIDE_DELETE_BUTTON:
                    if (deleteButton != null)
                    {
                        deleteButton.setVisibility(View.GONE);
                    }
                    break;

                case ENABLE_START_BUTTON:
                    if (startButton != null)
                    {
                        startButton.setEnabled(true);
                    }
                    break;

                case DISABLE_START_BUTTON:
                    if (startButton != null)
                    {
                        startButton.setEnabled(false);
                    }
                    break;

                case TOGGLE_START_BUTTON:
                    if (startButton != null)
                    {
                        startButton.setChecked(true);
                    }
                    break;

                case DISPLAY_INFO_TOAST:
                    String text = (String) msg.obj;
                    int duration = Toast.LENGTH_LONG;
                    Toast toast =
                        Toast.makeText(context, text, duration);
                    toast.show();
                    break;

                default:
                    break;
            }
        }
    }
    private MyActivityHandler mainActivityHandler;

    // Flags for our Handler:
    public static final int SHOW_DELETE_BUTTON = 0;
    public static final int HIDE_DELETE_BUTTON = 1;
    public static final int ENABLE_START_BUTTON = 2;
    public static final int DISABLE_START_BUTTON = 3;
    public static final int TOGGLE_START_BUTTON = 4;
    public static final int DISPLAY_INFO_TOAST = 5;

    // Native methods to handle button clicks:
    public native void nativeStart();
    public native void nativeClear();
    public native void nativeReset();
    public native void nativeDelete();


    /** Initialize the GUIManager. */
    public GUIManager(Context context)
    {
        // Load our overlay view:
        // NOTE: This view will add content on top of the camera / OpenGL view
        overlayView = View.inflate(context, R.layout.interface_overlay, null);

        // Create a Handler from the current thread:
        // This is the only thread that can make changes to the GUI,
        // so we require a handler for other threads to make changes
        mainActivityHandler = new MyActivityHandler(this, context);
    }


    /** Button clicks should call corresponding native functions. */
    public void initButtons()
    {
        if (overlayView == null)
            return;

        startButton = (ToggleButton)overlayView.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (((ToggleButton) v).isChecked())
                {
                    nativeStart();
                }
                else
                {
                    nativeReset();
                }
            }
        });

        clearButton = (Button) overlayView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nativeClear();
                startButton.setChecked(false);
            }
        });

        deleteButton = (Button) overlayView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nativeDelete();
            }
        });
    }


    /** Clear the button listeners. */
    public void deinitButtons()
    {
        if (overlayView == null)
            return;

        if (startButton != null)
        {
            startButton.setChecked(false);
            startButton.setOnClickListener(null);
            startButton = null;
        }

        if (clearButton != null)
        {
            clearButton.setOnClickListener(null);
            clearButton = null;
        }

        if (deleteButton != null)
        {
            deleteButton.setOnClickListener(null);
            deleteButton = null;
        }
    }


    /** Send a message to our gui thread handler. */
    public void sendThreadSafeGUIMessage(Message message)
    {
        mainActivityHandler.sendMessage(message);
    }


    /** Getter for the overlay view. */
    public View getOverlayView()
    {
        return overlayView;
    }
}

package org.pojava.util;

/*
 Copyright 2008-09 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.File;
import java.io.IOException;

/**
 * This tool runs an external program through your OS, and stuffs the results from stdout and
 * stderr into their own StringBuffers.
 * 
 * @author John Pile
 * 
 */
public class ProcessTool {

    /**
     * This method runs an executable, capturing its output from stdout and stderr into
     * StringBuffers.
     * 
     * @param cmdarray
     *            Executable plus arguments
     * @param envp
     *            Array of environmental variables
     * @param workDir
     *            Work folder. Null is current directory.
     * @param stdout
     *            StringBuffer to hold stdout.
     * @param stderr
     *            StringBuffer to hold stderr.
     * @throws IOException
     * @throws InterruptedException
     * @return integer return value of process executed
     */
    public static int exec(String[] cmdarray, String[] envp, File workDir, StringBuffer stdout,
            StringBuffer stderr) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(cmdarray, envp, workDir);
        DataPump errCapture = new DataPump(process.getErrorStream(), stderr);
        errCapture.start();
        DataPump outCapture = new DataPump(process.getInputStream(), stdout);
        outCapture.start();
        return process.waitFor();
    }

    /**
     * This method runs an executable, capturing its output from stdout and stderr into
     * StringBuffers.
     * 
     * @param command
     *            Command to execute
     * @param stdout
     *            Your StringBuffer for holding stdout
     * @param stderr
     *            Your StringBuffer for holding stderr
     * @return integer return value of process executed
     * @throws IOException
     * @throws InterruptedException
     */
    public static int exec(String command, StringBuffer stdout, StringBuffer stderr)
            throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        DataPump errCapture = new DataPump(process.getErrorStream(), stderr);
        errCapture.start();
        DataPump outCapture = new DataPump(process.getInputStream(), stdout);
        outCapture.start();
        return process.waitFor();
    }

}

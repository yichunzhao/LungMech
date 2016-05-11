#include <jni.h>
#include "DAConverter.h"
#include <windows.h>
#include "nidaq.h"
#include <stdio.h>
#include <conio.h>
#include <math.h>

JNIEXPORT jshort JNICALL Java_DAConverter_DAConverter_AOWrite(JNIEnv *env, jobject myself, jshort deviceNumber, jshort channel, jshort value)
{
  typedef i16 (WINAPI *PfnAO_Write)(i16,i16,i16);

  i16 deviceNum = deviceNumber ;
  i16 chan =  channel ;
  i16 binVal = value ;
  i16 wrstat = 0;


  // declare a function pointer to point to function access point
  PfnAO_Write pfnAO_Write;

  //get a handle to the nidaq32 DLL
  HINSTANCE hdaq = LoadLibrary("C:\LungMech\sharelib\nidaq32.dll");

  if (hdaq == 0) {
        //printf( "Unable to locate nidaq32.DLL!" );
        return 2;
    }

  //get a function pointer from nidaq32.DLL
  pfnAO_Write  = (PfnAO_Write)GetProcAddress(hdaq,"AO_Write");

  //write value on channel
  wrstat  = pfnAO_Write(deviceNum, chan, binVal);


  FreeLibrary("C:\LungMech\sharelib\nidaq32.dll");

  return wrstat;
}
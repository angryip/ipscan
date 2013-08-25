echo off
title Switching to original UI...
del modern.exe
del modern_headerbmp.exe
del modern_headerbmpr.exe
copy modern_original.exe modern.exe
copy modern_headerbmp_original.exe modern_headerbmp.exe
copy modern_headerbmpr_original.exe modern_headerbmpr.exe
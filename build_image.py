##############################################################################
#
#   PolyGlot build script Copyright 2019-2022 Draque Thompson
#
#   This script builds PolyGlot into a distributable package on Linux,
#   OSX, and Windows. Windows does not come with Python installed by default.
#   This runs on both Python 2.7 and 3.x. 
#
#   From: https://github.com/DraqueT/PolyGlot/
#
##############################################################################

import datetime
from datetime import date
import os
from os import path
import platform
import shutil
import sys
import time
import uuid
from xml.dom import minidom

buildResult = ''
copyDestination = ''
failFile = ''
osString = platform.system()

linString = 'Linux'
osxString = 'Darwin'
winString = 'Windows'

separatorCharacter = '/'
macIntelBuild = False
skipTests = False


###############################
# LINUX BUILD CONSTANTS
LIN_INS_NAME = 'PolyGlot-Ins-Lin.deb'


###############################
# OSX BUILD CONSTANTS
OSX_INS_NAME = 'PolyGlot-Ins-Osx.dmg'
OSX_INTEL_INS_NAME = 'PolyGlot-Ins-Osx-intel.dmg'
SIGN_IDENTITY = '' # set in main for timing reasons
DISTRIB_IDENTITY = '' # set in main for timing reasons


###############################
# WINDOWS BUILD CONSTANTS
WIN_INS_NAME = 'PolyGlot-Ins-Win.exe'


###############################
# UNIVERSAL BUILD CONSTANTS
# You will not need to change these
JAR_W_DEP = '' # set in main for timing reasons
JAR_WO_DEP = '' # set in main for timing reasons
JAVAFX_VER = '' # set in main for timing reasons
POLYGLOT_VERSION = '' # set in main for timing reasons
POLYGLOT_BUILD = '' # set in main for timing reasons
JAVA_HOME = '' # set in main for timing reasons
IS_RELEASE = False
CUR_YEAR = str(date.today().year)



######################################
#   PLATFORM AGNOSTIC FUNCTIONALITY
######################################

def main(args):
    global POLYGLOT_VERSION
    global POLYGLOT_BUILD
    global JAVA_HOME
    global SIGN_IDENTITY
    global DISTRIB_IDENTITY
    global IS_RELEASE
    global JAR_W_DEP
    global JAR_WO_DEP
    global JAVAFX_VER
    global failFile
    global copyDestination
    global separatorCharacter
    global macIntelBuild
    global skipTests

    if 'help' in args or '-help' in args or '--help' in args:
        printHelp()
        return
    
    skip_steps = []
    
    if osString == winString:
        separatorCharacter = '\\'

    if '-skipTests' in args:
        skipTests = True
        command_index = args.index('-skipTests')
        del args[command_index]

    # gather list of steps marked to be skipped
    while '-skip' in args:
        command_index = args.index('-skip')
        skip_steps.append(args[command_index + 1])
        
        print("Skipping: " + args[command_index + 1] + " step.")
        
        # remove args after consuming
        del args[command_index + 1]
        del args[command_index]

    # allows specifying code signing identity for mac builds
    if '-mac-sign-identity' in args:
        command_index = args.index('-mac-sign-identity')
        SIGN_IDENTITY = args[command_index + 1]
        
        # remove args after consuming
        del args[command_index + 1]
        del args[command_index]

    # allows specifying code signing for mac distribution
    if '-mac-distrib-cert' in args:
        command_index = args.index('-mac-distrib-cert')
        DISTRIB_IDENTITY = args[command_index + 1]
        
        # remove args after consuming
        del args[command_index + 1]
        del args[command_index]
    
    # allows for override of java home (virtual environments make this necessary at times)
    if '-java-home-o' in args:
        command_index = args.index('-java-home-o')
        print('JAVA_HOME overriden to: ' + args[command_index + 1])
        JAVA_HOME = args[command_index + 1]
        
        # remove args after consuming
        del args[command_index + 1]
        del args[command_index]
    else:
        JAVA_HOME = os.getenv('JAVA_HOME')

    # detects if marked for release
    if '-release' in args:
        command_index = args.index('-release')
        print('RELEASE BUILD')
        IS_RELEASE = True
        del args[command_index]
    else:
        print('BETA BUILD')
        
    if '-copyDestination' in args:
        command_index = args.index('-copyDestination')
        print('Destination for final install file set to: ' + args[command_index + 1])
        copyDestination = args[command_index + 1]
        
        # failure message file created here, deleted at end of process conditionally upon success
        failFile = copyDestination + separatorCharacter + osString + "_BUILD_FAILED"
        open(failFile, 'a').close()
        
        # remove args after consuming
        del args[command_index + 1]
        del args[command_index]

    if JAVA_HOME is None:
        print('JAVA_HOME must be set. If necessary, use -java-home-o command to override')
        return

    try:
        POLYGLOT_VERSION = getVersion()
        POLYGLOT_BUILD = getBuildNum();
        print('Building Version: ' + POLYGLOT_VERSION)
        updateVersionResource(POLYGLOT_VERSION)
        JAR_W_DEP = 'PolyGlotLinA-' + POLYGLOT_VERSION + '-jar-with-dependencies.jar'
        JAR_WO_DEP = 'PolyGlotLinA-' + POLYGLOT_VERSION + '.jar'
        JAVAFX_VER = getJfxVersion()

        if osString == winString:
            os.system('echo off')
        elif osString == osxString and '-intelBuild' in args:
            macIntelBuild = True
            command_index = args.index('-intelBuild')
            del args[command_index]

        fullBuild = (len(args) == 1) # length of 1 means no arguments (full build)
        
        if (fullBuild and 'docs' not in skip_steps) or 'docs' in args:
            injectDocs()
        if (fullBuild and 'build' not in skip_steps) or 'build' in args:
            build()
        if (fullBuild and 'clean' not in skip_steps) or 'clean' in args or 'image' in args:
            clean()
        if (fullBuild and 'image' not in skip_steps) or 'image' in args:
            image()
        if (fullBuild and 'dist' not in skip_steps) or 'dist' in args:
            dist()
    finally:
        if osString == osxString:
            resetMacRepo()
        
    print('Done!')

def build():
    print('Injecting build date/time...')
    injectBuildDate()
    if osString == linString:
        buildLinux()
    elif osString == osxString:
        buildOsx()
    elif osString == winString:
        buildWin()
    
def clean():
    if osString == linString:
        cleanLinux()
    elif osString == osxString:
        cleanOsx()
    elif osString == winString:
        cleanWin()
    
def image():
    global macIntelBuild

    if osString == linString:
        imageLinux()
    elif osString == osxString:
        setupMacRepo() # must be handled here due to earlier build pulling libraries
        imageOsx()
    elif osString == winString:
        imageWin()
    
def dist():
    if osString == linString:
        distLinux()
    elif osString == osxString:
        distOsx()
    elif osString == winString:
        distWin()


######################################
#       LINUX FUNCTIONALITY
######################################

def buildLinux():
    global skipTests
    print('cleaning/testing/compiling...')
    command = 'mvn clean package'

    if (skipTests):
        command += ' -DskipTests'

    os.system(command)

def cleanLinux():
    print('cleaning build paths...')
    os.system('rm -rf target/mods')
    os.system('rm -rf build')
    
def imageLinux():
    print('POLYGLOT_VERSION: ' + POLYGLOT_VERSION)
    print('creating jmod based on jar built without dependencies...')
    os.system('mkdir target/mods')
    os.system(JAVA_HOME + '/bin/jmod create ' +
        '--class-path target/' + JAR_WO_DEP + ' ' +
        '--main-class org.darisadesigns.polyglotlina.Desktop.PolyGlot target/mods/PolyGlot.jmod')

    JAVAFX_LOCATION = getJfxLocation()

    print('creating runnable image...')
    command = (JAVA_HOME + '/bin/jlink ' +
        '--module-path "module_injected_jars/:' +
        'target/mods:' +
        JAVAFX_LOCATION + '/javafx-graphics/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-base/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-media/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-swing/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-controls/' + JAVAFX_VER + '/:' +
        JAVA_HOME + '/jmods" ' +
        '--add-modules "org.darisadesigns.polyglotlina.polyglot","jdk.crypto.ec" ' +
        '--output "build/image/" ' +
        '--compress=2 ' +
        '--launcher PolyGlot=org.darisadesigns.polyglotlina.polyglot')

    os.system(command)
    
def distLinux():
    print('Creating distribution deb...')
    os.system('rm -rf installer')
    os.system('mkdir installer')
    command = (JAVA_HOME + '/bin/jpackage ' +
        '--app-version ' + POLYGLOT_BUILD + ' ' +
        '--copyright "2014-' + CUR_YEAR + ' Draque Thompson" ' +
        '--description "PolyGlot is a spoken language construction toolkit." ' +
        '--file-associations packaging_files/linux/file_types_linux.prop ' +
        '--icon packaging_files/PolyGlot0.png ' +
        '--linux-package-name polyglot-linear-a ' +
        '--linux-app-category Education ' +
        '--module org.darisadesigns.polyglotlina.polyglot/org.darisadesigns.polyglotlina.PolyGlot ' +
        '--name "PolyGlot" ' +
        '--license-file LICENSE.TXT ' +
        '--runtime-image build/image')
    os.system(command)
    
    if copyDestination != "":
        copyInstaller('polyglot-linear-a_' + POLYGLOT_BUILD + '-1_amd64.deb')


######################################
#       Mac OS FUNCTIONALITY
######################################

def buildOsx():
    global skipTests
    print('cleaning/testing/compiling...')
    command = 'mvn clean package'

    if skipTests:
        command += ' -DskipTests'

    os.system(command)
    
def cleanOsx():
    print('cleaning build paths...')
    os.system('rm -rf target/mods')
    os.system('rm -rf build')
    
def imageOsx():
    print('creating jmod based on jar built without dependencies...')
    os.system('mkdir target/mods')
    os.system(JAVA_HOME + '/bin/jmod create ' +
        '--class-path target/' + JAR_WO_DEP + ' ' +
        '--main-class org.darisadesigns.polyglotlina.Desktop.PolyGlot target/mods/PolyGlot.jmod')

    JAVAFX_LOCATION = getJfxLocation()

    print('creating runnable image...')
    os.system(JAVA_HOME + '/bin/jlink ' +
        '--module-path "module_injected_jars/:' +
        'target/mods:' +
        JAVAFX_LOCATION + '/javafx-graphics/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-base/' + JAVAFX_VER + '/:'+
        JAVAFX_LOCATION + '/javafx-media/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-swing/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/javafx-controls/' + JAVAFX_VER + '/:' +
        JAVAFX_LOCATION + '/jmods" ' +
        '--add-modules "org.darisadesigns.polyglotlina.polyglot","jdk.crypto.ec" ' +
        '--output "build/image/" ' +
        '--compress=2 ' +
        '--launcher PolyGlot=org.darisadesigns.polyglotlina.polyglot')
    
def distOsx():
    print('Creating app image...')
    
    command = (JAVA_HOME + '/bin/jpackage ' +
        '--runtime-image build/image ' +
        '--icon "PolyGlot.app" ' +
        '--name PolyGlot ' +
        '--module org.darisadesigns.polyglotlina.polyglot/org.darisadesigns.polyglotlina.PolyGlot ' +
        '--copyright "2014-' + CUR_YEAR + ' Draque Thompson" ' +
        '--description "PolyGlot is a spoken language construction toolkit." ' +
        '--type app-image ' +
        '--mac-package-name "PolyGlot" ' +
        '--file-associations packaging_files/mac/file_types_mac.prop ' +
        '--icon packaging_files/mac/PolyGlot.icns ' +
        '--app-version "' + POLYGLOT_VERSION + '"')

    os.system(command)

    # Remove the extra copy of libjli.dylib which causes notarization to fail
    if(path.exists('PolyGlot.app/Contents/runtime/Contents/MacOS/libjli.dylib')):
        os.remove('PolyGlot.app/Contents/runtime/Contents/MacOS/libjli.dylib')

    if SIGN_IDENTITY and not DISTRIB_IDENTITY: # only sign with dev identity
        print('Code signing app image with developer certificate...')
        command = ('codesign ' +
            '--force ' + # Overwrite existing signature
            '--timestamp ' + # Embed secure timestamps
            '--options runtime ' + # Enable hardened runtime
            '--entitlements packaging_files/mac/entitlements.plist ' + # Add entitlements
            '--sign "' + SIGN_IDENTITY + '" ' +
            'PolyGlot.app')

        os.system(command)
    elif not DISTRIB_IDENTITY:
        print('No code signing identity specified, app image will not be signed as developer')

    if DISTRIB_IDENTITY:
        print('Code signing app image with distribution certificate...')
        command = ('codesign ' +
            '--force ' + # Overwrite existing signature
            '--timestamp ' + # Embed secure timestamps
            '--options runtime ' + # Enable hardened runtime
            '--entitlements packaging_files/mac/entitlements.plist ' + # Add entitlements
            '--sign "' + DISTRIB_IDENTITY + '" ' +
            'PolyGlot.app')

        os.system(command)
    else:
        print('No distribution signing identity specified, app image will not be signed for distribution')

    POLYGLOT_DMG = 'PolyGlot-' + POLYGLOT_VERSION + '.dmg'

    try:
        print('Creating distribution package...')
        command = ('dmgbuild ' +
            '-s packaging_files/mac/dmg_settings.py PolyGlot ' + POLYGLOT_DMG)

        os.system(command)

        if DISTRIB_IDENTITY:
            print('Code signing dmg installer image with distribution certificate...')
            command = ('codesign ' +
                '--timestamp ' + # Embed secure timestamps
                '--entitlements packaging_files/mac/entitlements.plist ' + # Add entitlements
                '--sign "' + DISTRIB_IDENTITY + '" ' + POLYGLOT_DMG)

            os.system(command)
        else:
            print('No distribution signing identity specified, dmg installer will not be signed for distribution')

        if copyDestination != "":
            copyInstaller('PolyGlot-' + POLYGLOT_VERSION + '.dmg')

    except:
        print('\'dmgbuild\' does not exist in PATH, distribution packaging will be skipped')
        print('Run \'pip install dmgbuild\' to install it')

    # cleanup created app
    if(path.exists('PolyGlot.app')):
        shutil.rmtree('PolyGlot.app')

# This prevents the build from getting confused from multiple versions of libraries existing (intel and Arch64)
def setupMacRepo():
    global macIntelBuild
    print('setting up mac repo for ' + ('intel ' if macIntelBuild else 'arch64 ') + 'build...')
    ignoreJavaFxFile('base')
    ignoreJavaFxFile('controls')
    ignoreJavaFxFile('graphics')
    ignoreJavaFxFile('media')
    ignoreJavaFxFile('swing')

def ignoreJavaFxFile(fileModule):
    global JAVAFX_VER
    global macIntelBuild
    JAVAFX_LOCATION = getJfxLocation();

    jfxJar = JAVAFX_LOCATION + '/javafx-' + fileModule + '/' + JAVAFX_VER+ '/javafx-' + fileModule + '-' + JAVAFX_VER + '-mac.jar' if not macIntelBuild \
        else JAVAFX_LOCATION + '/javafx-' + fileModule + '/' + JAVAFX_VER+ '/javafx-' + fileModule + '-' + JAVAFX_VER + '-mac-aarch64.jar'

    if path.exists(jfxJar):
        print('Temporarily ignoring: ' + jfxJar)
        os.rename(jfxJar, jfxJar + '.ignore')
    else:
        raise Exception("JavaFx File Missing: " + jfxJar)

# Resets files from setupMacRepo step
def resetMacRepo():
    print('resetting mac repo files...')
    unignoreJavaFxFile('base')
    unignoreJavaFxFile('controls')
    unignoreJavaFxFile('graphics')
    unignoreJavaFxFile('media')
    unignoreJavaFxFile('swing')

def unignoreJavaFxFile(fileModule):
    global JAVAFX_VER
    JAVAFX_LOCATION = getJfxLocation();

    jfxIntelJar = JAVAFX_LOCATION + '/javafx-' + fileModule + '/' + JAVAFX_VER + '/javafx-' + fileModule + '-' + JAVAFX_VER + '-mac.jar'
    jfxArch = JAVAFX_LOCATION + '/javafx-' + fileModule + '/' + JAVAFX_VER + '/javafx-' + fileModule + '-' + JAVAFX_VER + '-mac-aarch64.jar'

    if path.exists(jfxIntelJar + '.ignore'):
        print('Unignoring: ' + jfxIntelJar)
        os.rename(jfxIntelJar + '.ignore', jfxIntelJar)

    if path.exists(jfxArch + '.ignore'):
        print('Unignoring: ' + jfxArch)
        os.rename(jfxArch + '.ignore', jfxArch)


######################################
#       WINDOWS FUNCTIONALITY
######################################

def buildWin():
    global skipTests
    print('cleaning/testing/compiling...')
    command = 'mvn clean package'

    if skipTests:
        command += ' -DskipTests'

    os.system(command)
    
def cleanWin():
    print('cleaning build paths...')
    os.system('rmdir target\mods /s /q')
    os.system('rmdir build /s /q')
    
def imageWin():
    print('creating jmod based on jar built without dependencies...')
    os.system('mkdir target\mods')
    os.system('jmod create ' +
        '--class-path target\\' + JAR_WO_DEP +
        ' --main-class org.darisadesigns.polyglotlina.Desktop.PolyGlot ' +
        'target\mods\PolyGlot.jmod')

    JAVAFX_LOCATION = getJfxLocation()

    print('creating runnable image...')
    command = ('jlink ' +
        '--module-path "module_injected_jars;' +
        'target\\mods;' +
        JAVAFX_LOCATION + '\\javafx-graphics\\' + JAVAFX_VER + ';' +
        JAVAFX_LOCATION + '\\javafx-base\\' + JAVAFX_VER + ';' +
        JAVAFX_LOCATION + '\\javafx-media\\' + JAVAFX_VER + ';' +
        JAVAFX_LOCATION + '\\javafx-swing\\' + JAVAFX_VER + ';' +
        JAVAFX_LOCATION + '\\javafx-controls\\' + JAVAFX_VER + ';' +
        '%JAVA_HOME%\jmods" ' +
        '--add-modules "org.darisadesigns.polyglotlina.polyglot","jdk.crypto.ec" ' +
        '--output "build\image" ' +
        '--compress=2 ' +
        '--launcher PolyGlot=org.darisadesigns.polyglotlina.polyglot')
    os.system(command)

def distWin():
    packageLocation = 'PolyGlot-' + POLYGLOT_BUILD + '.exe'
    print('Creating distribution package...')
    os.system('rmdir /s /q installer')

    # If missing, install WiX Toolset: https://wixtoolset.org/releases/
    command = ('jpackage ' + 
        '--runtime-image build\\image ' +
        '--win-shortcut ' +
        '--win-menu ' +
        '--win-dir-chooser ' +
        '--file-associations packaging_files\\win\\file_types_win.prop ' +
        '--name PolyGlot ' +
        '--module org.darisadesigns.polyglotlina.polyglot/org.darisadesigns.polyglotlina.PolyGlot ' +
        '--copyright "2014-' + CUR_YEAR + ' Draque Thompson" ' +
        '--description "PolyGlot is a spoken language construction toolkit." ' +
        '--app-version "' + POLYGLOT_BUILD + '" ' +
        '--license-file LICENSE.TXT ' +
        '--win-upgrade-uuid  ' + str(uuid.uuid4()) + ' ' + # Unique identifier to keep versioned installers from erroring in Windows
        '--icon packaging_files/win/PolyGlot0.ico')

    os.system(command)
    
    if copyDestination != "":
        copyInstaller(packageLocation)

# injects current time into file which lives in PolyGlot resources
def injectBuildDate():
    buildTime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M')
    filePath = 'assets/assets/org/DarisaDesigns/buildDate'
    
    if (osString == winString):
        filePath = filePath.replace('/', '\\')
    
    f = open(filePath, 'w')
    f.write(buildTime)
    f.close()

####################################
#       UTIL FUNCTIONALITY
####################################

# handled here for timing reasons...
def getJfxLocation():
    ret = os.path.expanduser('~')

    if (osString == winString):
        ret += '\\.m2\\repository\\org\\openjfx'
    elif (osString == osxString or osString == linString):
        ret += '/.m2/repository/org/openjfx'

    return ret

# What it says on the tin
def getJfxVersion():
    ret = ''
    mydoc = minidom.parse('pom.xml')
    dependencies = mydoc.getElementsByTagName('dependency')
    
    for dependency in dependencies:
        if (dependency.getElementsByTagName('groupId')[0].childNodes[0].nodeValue == 'org.openjfx'):
            ret = dependency.getElementsByTagName('version')[0].childNodes[0].nodeValue
            break
    return ret

# fetches version of PolyGlot from pom file
def getVersion():
    mydoc = minidom.parse('pom.xml')
    versionItems = mydoc.getElementsByTagName('version')
    
    return versionItems[0].firstChild.data

# for releases, this will match the version. For beta builds, a UTC timestamp is appended (OS registration reasons on install)
def getBuildNum():
    ret = getVersion()
    
    if not IS_RELEASE and osString == winString:
        # truncate build from version string if present
        if ret.count('.') > 1:
            ret = ret[0:ret.rfind('.')]
        
        if osString == winString:
            # windows has max build num of 65535
            autoBuildNum = int(time.time()) # base build on system time
            autoBuildNum = autoBuildNum / 100 # truncate by 100 seconds (max of one build per 16 mins 40 seconds)
            autoBuildNum = int(autoBuildNum % 65535) # reduce build to number between 0 - 65534
            ret = ret + '.' + str(autoBuildNum)
        else:
            ret = ret + '.' + str(int(time.time()))

    return ret

def updateVersionResource(versionString):
    global IS_RELEASE
    
    if osString == winString:
        location = 'assets\\assets\\org\\DarisaDesigns\\version'
    else:
        location = 'assets/assets/org/DarisaDesigns/version'
    
    if path.exists(location):
        os.remove(location)
    
    with open(location, 'w+') as versionFile:
        if IS_RELEASE:
            versionFile.write(versionString)
        else:
            versionFile.write(versionString + 'B')

# Injects readme (with resources), example dictionaries, etc.
def injectDocs():
    print('Injecting documentation...')

    # readme and resources...
    extension = '.zip'
    if osString == winString:
        readmeLocation = 'assets\\assets\\org\\DarisaDesigns\\readme'
    else:
        readmeLocation = 'assets/assets/org/DarisaDesigns/readme'

    if path.exists(readmeLocation + extension):
        os.remove(readmeLocation + extension)

    shutil.make_archive(readmeLocation, 'zip', 'docs')

    # example dictionaries
    if osString == winString:
        sourceLocation = 'packaging_files\\example_lexicons'
        dictLocation = 'assets\\assets\\org\\DarisaDesigns\\exlex'
    else:
        sourceLocation = 'packaging_files/example_lexicons'
        dictLocation = 'assets/assets/org/DarisaDesigns/exlex'

    if path.exists(sourceLocation + extension):
        os.remove(readmeLocation + extension)

    shutil.make_archive(dictLocation, 'zip', sourceLocation)
    
# Copies installer file to final destination and removes error indicator file
def copyInstaller(source):
    global copyDestination
    global macIntelBuild

    if path.exists(source):
        if osString == winString:
            insFile = WIN_INS_NAME
        elif osString == linString:
            insFile = LIN_INS_NAME
        elif osString == osxString and macIntelBuild:
            insFile = OSX_INTEL_INS_NAME
        elif osString == osxString:
            insFile = OSX_INS_NAME

        # release candidates copied to their own location
        if IS_RELEASE:
            copyDestination = copyDestination + separatorCharacter + 'Release'

        destination = copyDestination + separatorCharacter + insFile
        print('Copying installer to ' + destination)
        shutil.copy(source, destination)
    
        # only remove failure signal once process is successful
        os.remove(failFile)
        os.remove(source)
    else:
        print('FAILURE: Built installer missing: ' + source)

def printHelp():
    print("""
#################################################
#       PolyGlot Build Script
#################################################

To use this utility, simply execute this script with no arguments to run the entire application construction sequence. To target particular steps, use any combination of the following arguments:

    docs : Zips and injects documentation into the application assets.

    build : Performs a maven build. creates both the jar with and the jar without dependencies included. Produced files stored in the target folder.
    
    clean : Wipes the product of build.

    image : From the built jar files (which must exist), creates a runnable image. This image is platform dependent. Produced files stored in the build folder.
    
    dist : Creates distribution files for the application. This is platform dependent. Produced files stored in the installer folder.

    -java-home-o <jdk-path> : Overrides JAVA_HOME. Useful for stubborn VMs that will not normally recognize environment variables.
    
    -mac-sign-identity <identity> : Sign the Mac app image with the specified code signing identity.

    -copyDestination <destination-path> : sets location for the final created installer file to be copied to (ignored if distribution not built)
    
    -skip <step> : skips the given step (can be used multiple times)
    
    -release : marks build as release build. Otherwise will be build as beta

    -skipTests : skips test step in Maven

    -intelBuild : MacOS only, indicates to use intel libraries rather than Arch64 when building

Example: python build_image.py image pack -java-home-o /usr/lib/jvm/jdk-14

The above will presume that the maven build has already taken place. It will use the produced jar files to create a runnable image, then from that image, create a packed application for the platform you are currently running. The JAVA_HOME path is overridden to point to /usr/lib/jvm/jdk-14.
""")

if __name__ == "__main__":
    main(sys.argv)
    

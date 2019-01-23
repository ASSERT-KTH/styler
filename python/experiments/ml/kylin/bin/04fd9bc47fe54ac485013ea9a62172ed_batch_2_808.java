/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.common.persistence;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kylin.common.KylinConfig;
import org.apache.kylin.common.

util .BytesUtil;importorg.
apache .kylin.common.util.HadoopUtil;importorg.
slf4j .Logger;importorg.
slf4j .LoggerFactory;/**
 * A big resource may not fit in a store cell. When that happens, HDFS becomes a fallback storage.
 *
 * This class helps to pushdown big resource to HDFS.
 * - An empty byte array is saved to ResourceStore as an indicator of pushdown.
 * - The big resource is saved as HDFS file according to its resource path.
 * - Method like checkAndPut() does not work on big resource like such, because HDFS lack of transaction support.
 */abstractpublic

class
PushdownResourceStore extends ResourceStore { private static final
    Logger logger = LoggerFactory . getLogger (HDFSResourceStore.class);protectedPushdownResourceStore(

    KylinConfig kylinConfig){ super( kylinConfig
        );}finalprotected
    void

    putResourceImpl ( String resPath,ContentWriter content, long ts) throws IOException{ if ( content
        . isBigContent())putBigResource(resPath
            ,content,ts ); elseputSmallResource(
        resPath
            ,content,ts ); }abstractprotected
    void

    putSmallResource ( String resPath,ContentWriter content, long ts) throws IOException; final voidputBigResource

    ( String resPath,ContentWriter content, long newTS) throws IOException{ // pushdown the big resource to DFS file RollbackablePushdown pushdown

        =
        writePushdown ( resPath ,content); try{// write a marker in resource store, to indicate the resource is now available

        logger .
            debug
            ("Writing marker for big resource {}",resPath); putResourceWithRetry(resPath
            ,ContentWriter.create (BytesUtil.EMPTY_BYTE_ARRAY),newTS); }catch(

        Throwable ex ){ pushdown. rollback
            ();throwex;
            } finally{
        pushdown . close
            ();}}protected
        RollbackablePushdown
    writePushdown

    ( String resPath,ContentWriter content) throws IOException{ return new RollbackablePushdown
        ( resPath ,content); }publicclass
    RollbackablePushdown

    implements AutoCloseable { FileSystem fs ;
        Path tempPath;
        Path realPath;
        Path backPath;
        boolean hasOldFile;
        boolean hasRollback=
        false ; private RollbackablePushdown(

        String resPath,ContentWriter content) throws IOException{ int salt =
            System . identityHashCode (resPath)+System. identityHashCode (content);tempPath=pushdownPath
            ( resPath +".temp."+ salt ) ; realPath=pushdownPath
            ( resPath );backPath=pushdownPath
            ( resPath +".orig."+ salt ) ; fs=pushdownFS
            ( ) ;if(fs

            . exists(tempPath))fs.delete
                (tempPath,true); logger.debug

            ("Writing pushdown file {}",tempPath); try(DataOutputStream
            out =fs . create (tempPath,true)) {content. write
                (out);}catch(
            IOException ex ){ close( )
                ;throwex;
                } try{
            hasOldFile

            = fs
                . exists (realPath);if(hasOldFile
                ) {logger. debug
                    ("Backup {} to {}",realPath,backPath ); fs.rename
                    (realPath,backPath); }}catch
                (
            IOException ex ){ close( )
                ;throwex;
                } logger.
            debug

            ("Move {} to {}",tempPath,realPath ); try{fs
            . rename
                (tempPath,realPath); }catch(
            IOException ex ){ rollback( )
                ;close()
                ;throwex;
                } }public
            void
        rollback

        ( ) {if( hasRollback
            ) return;hasRollback
                =true

            ; try {logger

            . error
                ("Rollback {} from {}",realPath,hasOldFile ?backPath . toString ():"<empty>") ; if(fs

                . exists(realPath))fs.delete
                    (realPath,true); if(hasOldFile

                ) fs.rename
                    (backPath,realPath); }catch(

            IOException ex2 ){ logger. error
                ("Rollback failed",ex2); }}@
            Override
        public

        voidclose
        ( ) {try{ if
            ( fs
                . exists(tempPath))fs.delete
                    (tempPath,true); }catch(
            IOException e ){ logger. error
                ("Error cleaning up "+tempPath, e ); }try{
            if

            ( fs
                . exists(backPath))fs.delete
                    (backPath,true); }catch(
            IOException e ){ logger. error
                ("Error cleaning up "+backPath, e ); }}}
            protected
        InputStream

    openPushdown

    ( String resPath)throws IOException{ try { Path
        p =
            pushdownPath ( resPath );FileSystemfs=
            pushdownFS ( ) ;if(fs
            . exists(p))returnfs.
                open (p);elsethrownew
            FileNotFoundException
                ( p .toString()+"  (FS: "+ fs + ")" ) ; }catch(

        Exception ex ){ thrownew IOException
            ( "Failed to read big resource " +resPath, ex ); }}abstract
        protected
    String

    pushdownRootPath ( ) ;protectedFileSystempushdownFS

    ( ) {returnHadoopUtil .
        getFileSystem (newPath(kylinConfig .getMetastoreBigCellHdfsDirectory()));}finalprotected
    Path

    pushdownPath ( String resPath){ Pathp =
        new Path ( pushdownRootPath ()+resPath) ; returnPath.
        getPathWithoutSchemeAndAuthority (p);}protectedvoid
    deletePushdown

    ( String resPath)throws IOException{ deletePushdownFile ( pushdownPath
        (resPath));}privatevoid
    deletePushdownFile

    ( Path path)throws IOException{ FileSystem fileSystem =
        pushdownFS ( ) ;if(fileSystem

        . exists(path)){fileSystem. delete
            (path,true); logger.debug
            ("Delete temp file success. Temp file: {} .",path); }else{
        logger . debug
            ("{} is not exists in the file system.",path); }}}
        
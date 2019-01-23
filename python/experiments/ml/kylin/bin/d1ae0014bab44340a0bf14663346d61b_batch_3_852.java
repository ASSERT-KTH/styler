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

package org.apache.kylin.job.execution

; importjava.util.Map

; importorg.apache.commons.lang3.StringUtils

;
/**
 */ public class DefaultOutput implements Output

    { private ExecutableStatestate
    ; privateMap<String ,String >extra
    ; private StringverboseMsg
    ; private longlastModified

    ;@
    Override publicMap<String ,String >getExtra( )
        { returnextra
    ;

    }@
    Override public StringgetVerboseMsg( )
        { returnverboseMsg
    ;

    }@
    Override public ExecutableStategetState( )
        { returnstate
    ;

    }@
    Override public longgetLastModified( )
        { returnlastModified
    ;

    } public voidsetState( ExecutableStatestate )
        {this. state =state
    ;

    } public voidsetExtra(Map<String ,String >extra )
        {this. extra =extra
    ;

    } public voidsetVerboseMsg( StringverboseMsg )
        {this. verboseMsg =verboseMsg
    ;

    } public voidsetLastModified( longlastModified )
        {this. lastModified =lastModified
    ;

    }@
    Override public inthashCode( )
        { final int prime =31
        ; int hashCode =state.hashCode()
        ; hashCode = hashCode * prime +extra.hashCode()
        ; hashCode = hashCode * prime +verboseMsg.hashCode()
        ; hashCode = hashCode * prime +Long.valueOf(lastModified).hashCode()
        ; returnhashCode
    ;

    }@
    Override public booleanequals( Objectobj )
        { if(!( obj instanceofDefaultOutput) )
            { returnfalse
        ;
        } DefaultOutput another =((DefaultOutput )obj)
        ; if(this. state !=another.state )
            { returnfalse
        ;
        } if(!extra.equals(another.extra) )
            { returnfalse
        ;
        } if(this. lastModified !=another.lastModified )
            { returnfalse
        ;
        } returnStringUtils.equals(verboseMsg ,another.verboseMsg)
    ;
}

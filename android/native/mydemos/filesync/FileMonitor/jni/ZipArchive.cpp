#include "StdAfx.h"
#include <dirent.h>
#include "ZipArchive.h"

/******************************************************************************
 *
 *****************************************************************************/

#define MAX_COMMENT                    65535
#define READ_BUFFER_SIZE               4096
#define STREAMBUF_SIZE                 16384

#define LOCAL_FILE_HEADER_MAGIC        0x04034b50
#define CENTRAL_FILE_HEADER_MAGIC      0x02014b50
#define END_OF_CENTRAL_DIRECTORY_MAGIC 0x06054B50

/******************************************************************************
 *
 *****************************************************************************/
#if defined(__BORLANDC__)

 /******************************************************************************
  * BORLAND C Specific macros
  *****************************************************************************/
  #define _itoa    itoa
  #define _stricmp stricmp
  #define _ultoa   ultoa

  #define USES_CONVERSION
  #define A2CT(x)         (x)
  #define T2CA(x)         (x)

  TMemPool *CTN_ListNode< TZipFile >::pMemPool = NULL;

#elif defined( _PALM_ ) && !defined( T2CA )

 /******************************************************************************
  * Palm Specific macros
  *****************************************************************************/
  static char ms_T2CABuffer[1024];

  LPCSTR T2CA( LPCTSTR str ) {

    wcstombs( ms_T2CABuffer, str, sizeof(ms_T2CABuffer) );
    return ms_T2CABuffer;
    }

#endif

/******************************************************************************
 *
 *****************************************************************************/
#ifndef min
#define min(a,b)            (((a) < (b)) ? (a) : (b))
#endif



int strcncmp(const char *str1, const char *str2, size_t n)
{
    LOGI("Strncmp Entry %s and %s\n", str1, str2);

    for (;;) {
        if (n-- == 0) {
            LOGI("Strncmp Exit1\n");
            return 0;
        }
        if (*str1 != *str2) {
            int n1 = *str1;
            int n2 = *str2;
            
            if (n2 != n1) {
                return n2 > n1 ? -1 : 1;
            }
        } else {
            if ((*str1 == '\0') || (*str2 == '\0')) {
                LOGI("Strncmp Exit2\n");
                return 0;
            }
        }
        ++str1;
        ++str2;
    }
}



/******************************************************************************
 *
 *****************************************************************************/
size_t DuplicateStr( char **pDst, const char *lpStr ) {

  size_t RetVal = 0;

  if ( lpStr != NULL ) {

    RetVal = strlen( lpStr );
    
    if ( pDst != NULL ) {
      *pDst = new char[ RetVal + 1 ];
      strcpy( *pDst, lpStr );
      }
    }
  else if ( pDst != NULL ) {

    *pDst = NULL;
    }
  
  return RetVal;
  }

/******************************************************************************
 *
 *****************************************************************************/
bool ReplaceChar( char *lpBuffer, char cOriginal, char cNew ) {

  char *lpCur;
  while ( ( lpCur = strchr( lpBuffer, cOriginal ) ) != NULL ) {
    *lpCur = cNew;
    }

  return true;
  }

/******************************************************************************
 *
 *****************************************************************************/
char* FindFileName( char *lpPath , BYTE byIsUnixMode) {


  char *lpRetVal = NULL;
  
  if (0 == byIsUnixMode)
  {
	  lpRetVal = strrchr( lpPath, '\\' );
  }
  else
  {
	  lpRetVal = strrchr( lpPath, '/');
  }
  
  if ( lpRetVal != NULL ) {
    lpRetVal++;
    }
  else {
    lpRetVal = lpPath;
    }

  return lpRetVal;
  }

/******************************************************************************
 *
 *****************************************************************************/
char* AppendFilePath( char* lpDst, const char* lpSrc, BYTE byIsUnixMode ) {

  size_t End = strlen( lpDst );
  
  if (FALSE == byIsUnixMode) {
	  if ( End > 0 && lpDst[End-1] != '\\' && *lpSrc != '\\' ) {
		*(lpDst + End++) = '\\';
		}
  }else{
	    if ( End > 0 && lpDst[End-1] != '/' && *lpSrc != '/' ) {
			*(lpDst + End++) = '/';
		}
  }

  strcpy( lpDst + End, lpSrc );

  return lpDst;
  }


/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipFile::TZipFile() {

  m_lpFileName = NULL;
  m_lpComment = NULL;
  m_bDirty = false;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipFile::TZipFile( const TZipFileHeaderRec &Header, const char *lpFileName, const char *lpComment ) {

  m_Header = Header;
  m_Header.FileNameLen    = DuplicateStr( &m_lpFileName, lpFileName );
  m_Header.FileCommentLen = DuplicateStr( &m_lpComment,  lpComment );
  m_bDirty = false;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipFile::TZipFile( const TZipFile &A ) {

  m_Header = A.m_Header;
  m_Header.FileNameLen    = DuplicateStr( &m_lpFileName, A.m_lpFileName );
  m_Header.FileCommentLen = DuplicateStr( &m_lpComment,  A.m_lpComment );
  m_bDirty = A.m_bDirty;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipFile::~TZipFile() {

  Clear();
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::GetLocalHeader( TZipLocalFileHeaderRec *pLocalHeader ) const {

  pLocalHeader->Signature              = LOCAL_FILE_HEADER_MAGIC;
  pLocalHeader->VersionNeededToExtract = m_Header.VersionNeededToExtract;
  pLocalHeader->GeneralBitFlags        = m_Header.GeneralBitFlags;
  pLocalHeader->CompressionMethod      = m_Header.CompressionMethod;
  pLocalHeader->LastModifiedTime       = m_Header.LastModifiedTime;
  pLocalHeader->LastModifiedDate       = m_Header.LastModifiedDate;
  pLocalHeader->Crc32                  = m_Header.Crc32;
  pLocalHeader->CompressedSize         = m_Header.CompressedSize;
  pLocalHeader->UncompressedSize       = m_Header.UncompressedSize;
  pLocalHeader->FileNameLen            = m_Header.FileNameLen;
  pLocalHeader->ExtraFieldLen          = 0;

  return true;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::CompareLocalHeader( const TZipLocalFileHeaderRec &LocalHeader ) const {

  return LocalHeader.Signature              == LOCAL_FILE_HEADER_MAGIC
      && LocalHeader.VersionNeededToExtract == m_Header.VersionNeededToExtract
      && LocalHeader.GeneralBitFlags        == m_Header.GeneralBitFlags
      && LocalHeader.CompressionMethod      == m_Header.CompressionMethod
      && LocalHeader.LastModifiedTime       == m_Header.LastModifiedTime
      && LocalHeader.LastModifiedDate       == m_Header.LastModifiedDate
      && LocalHeader.Crc32                  == m_Header.Crc32
      && LocalHeader.CompressedSize         == m_Header.CompressedSize
      && LocalHeader.UncompressedSize       == m_Header.UncompressedSize
      && LocalHeader.FileNameLen            == m_Header.FileNameLen;
  }


/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
void TZipFile::Clear() {

  if ( m_lpFileName != NULL ) {
    delete[] m_lpFileName;
    m_lpFileName = NULL;
    }

  if ( m_lpComment != NULL ) {
    delete[] m_lpComment;
    m_lpComment = NULL;
    }

  m_bDirty = false;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::ReadCentralHeader( TZipStream &Stream ) {

  Clear();

  return Stream.ReadData( &m_Header, sizeof(m_Header) )
      && m_Header.Signature == CENTRAL_FILE_HEADER_MAGIC
      && ( m_Header.CompressionMethod == 0 || m_Header.CompressionMethod == Z_DEFLATED )
      && Stream.ReadString( &m_lpFileName, m_Header.FileNameLen ) && ReplaceChar( m_lpFileName, '/', '\\' )
      && Stream.Seek( m_Header.ExtraFieldLen, FILE_CURRENT ) != -1
      && Stream.ReadString( &m_lpComment, m_Header.FileCommentLen );
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::WriteCentralHeader( TZipStream &Stream ) {

  return Stream.WriteData( &m_Header, sizeof(m_Header) )
      && ReplaceChar( m_lpFileName, '\\', '/' )
      && Stream.WriteData( m_lpFileName, m_Header.FileNameLen )
      && ReplaceChar( m_lpFileName, '/', '\\' )
      && Stream.WriteData( m_lpComment,  m_Header.FileCommentLen );
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::WriteLocalHeader( TZipStream &Stream ) {

  TZipLocalFileHeaderRec LocalHeader;
  bool bRetVal = Stream.Seek( m_Header.OffsetOfLocalHeader, FILE_BEGIN ) != -1;
  bRetVal = GetLocalHeader( &LocalHeader );
  bRetVal = Stream.WriteData( &LocalHeader, sizeof(LocalHeader) );
  LOGI ("::WriteLocalHeaderb LocalHeader write RetVal = %d\n", bRetVal);
  bRetVal = ReplaceChar( m_lpFileName, '\\', '/' );
  LOGI ("::WriteLocalHeaderb ReplaceChar1 RetVal = %d\n", bRetVal);
  bRetVal = Stream.WriteData( m_lpFileName, m_Header.FileNameLen );
  LOGI ("::WriteLocalHeaderb m_lpFileName write RetVal = %d, Filename = %s, len = %d\n", bRetVal, m_lpFileName, m_Header.FileNameLen);
  bRetVal = ReplaceChar( m_lpFileName, '/', '\\' );
  LOGI ("::WriteLocalHeaderb Replacechar2 RetVal = %d\n", bRetVal);
  bRetVal = Stream.WriteData( m_lpComment,  m_Header.FileCommentLen );
  LOGI ("::WriteLocalHeaderb m_lpComment write RetVal = %d; len = %d, comment = %s\n", bRetVal, m_Header.FileCommentLen, m_lpComment);

  if ( bRetVal ) {

    //*** No longer dirty after the local header is written succesfully
	LOGI ("mDirty set to FALSE\n");
    m_bDirty = false;
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::VerifyLocalHeader( TZipStream &Stream, TZipLocalFileHeaderRec *pFileHeader ) {

  return Stream.Seek( m_Header.OffsetOfLocalHeader, FILE_BEGIN ) != -1
      && Stream.ReadData( pFileHeader, sizeof(*pFileHeader) )
      && CompareLocalHeader( *pFileHeader );
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipFile::WriteFile( TZipStream &Stream, const void *lpBuffer, size_t nSize ) {

  size_t nCompressed = 0;
  LOGI ("::WriteFile CAlling Stream.Write\n");
  size_t nRetVal = Stream.Write( lpBuffer, nSize, &nCompressed );
  LOGI ("::WriteFile Stream.Write returned %d\n", nRetVal);

  if ( nRetVal > 0 ) {

    if ( m_Header.UncompressedSize == 0 ) {
      m_Header.Crc32 = crc32( 0L, Z_NULL, 0 );
      }

    m_Header.Crc32 = crc32( m_Header.Crc32, (const Bytef *)lpBuffer, nRetVal );
    LOGI ("::WriteFile m_Header.Crc32 = %d\n", m_Header.Crc32);
    }

  m_Header.UncompressedSize += nRetVal;
  m_Header.CompressedSize   += nCompressed;
  m_bDirty |= ( nRetVal > 0 || nCompressed > 0 );

  return nRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipFile::DeflateEnd( TZipStream &Stream ) {

  size_t nCompressed = 0;
  bool bRetVal = Stream.DeflateEnd( &nCompressed );

  m_Header.CompressedSize += nCompressed;
  m_bDirty |= ( nCompressed > 0 );
  LOGI ("::DeflateEnd File nCompressed = %d, hdr.compressedsixe = %d, m_bDirty = %d\n", nCompressed, m_Header.CompressedSize, m_bDirty);
  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipStream::TZipStream() {

  m_lpFileName = NULL;
#ifndef ANDROID
  m_hFile = INVALID_HANDLE_VALUE;
#else  
  m_hFile = NULL;
#endif

  m_State = ZIPSTREAM_RAW;
  m_StreamBuf = NULL;

  m_Stream.next_in   = Z_NULL;
  m_Stream.avail_in  = Z_NULL;
  m_Stream.total_in  = Z_NULL;
  m_Stream.next_out  = Z_NULL;
  m_Stream.avail_out = Z_NULL;
  m_Stream.total_out = Z_NULL;
  m_Stream.zalloc    = Z_NULL;
  m_Stream.zfree     = Z_NULL;
  m_Stream.opaque    = Z_NULL;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipStream::~TZipStream() {

  Close();
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::Open
( char *lpFileName, DWORD dwCreateDisposition ) {

  Close();
#ifndef ANDROID
  DWORD dwDesiredAccess = GENERIC_READ | GENERIC_WRITE;
  DWORD dwShareMode = 0;

  m_lpFileName = _tcsdup( lpFileName );

  m_hFile = CreateFile(
                        lpFileName,
                        dwDesiredAccess,
                        dwShareMode,
                        NULL,
                        dwCreateDisposition,
                        FILE_ATTRIBUTE_NORMAL,
                        NULL );

  return m_hFile != INVALID_HANDLE_VALUE;
#else
  m_hFile = fopen (lpFileName, "w+b");
  return (NULL != m_hFile);
#endif
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
void TZipStream::Close() {

  if ( m_lpFileName != NULL ) {
    
    free( m_lpFileName );
    m_lpFileName = NULL;
    }
#ifndef ANDROID
  if ( m_hFile != INVALID_HANDLE_VALUE ) {

    CloseHandle( m_hFile );
    m_hFile = INVALID_HANDLE_VALUE;
    }
#else
  if ( m_hFile != NULL)
  {
	  fflush (m_hFile);
	  fclose (m_hFile);
	  m_hFile = NULL;
  }
#endif
  if ( m_StreamBuf != NULL ) {

    delete[] m_StreamBuf;
    m_StreamBuf = NULL;
    }
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipStream::Seek( long Distance, long Method ) {

  if ( m_State != ZIPSTREAM_RAW ) return -1;

  size_t tSize = 0;
  printf ("Seeked to %d, Method = %d\n", (int)Distance, Method);

#ifndef ANDROID
  tSize = SetFilePointer( m_hFile, Distance, NULL, Method );
#else
  if (NULL != m_hFile)
  {
	  fflush(m_hFile);
	  fseek (m_hFile, Distance, Method);
	  tSize =  ftell(m_hFile);
  }
#endif

  printf ("File seeked to %d\n", (int)tSize);
  return tSize;
  }
/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::InflateInit() {

  //*** Can't init if we're already in a inflate/deflate state
  if ( m_State != ZIPSTREAM_RAW ) return false;

  m_Stream.next_in   = Z_NULL;
  m_Stream.avail_in  = Z_NULL;
  m_Stream.total_in  = Z_NULL;
  m_Stream.next_out  = Z_NULL;
  m_Stream.avail_out = Z_NULL;
  m_Stream.total_out = Z_NULL;
  m_Stream.zalloc    = Z_NULL;
  m_Stream.zfree     = Z_NULL;
  m_Stream.opaque    = Z_NULL;

  bool bRetVal = inflateInit2( &m_Stream, -MAX_WBITS ) == Z_OK;
  if ( bRetVal ) {

    //*** Set our state to inflating
    m_State = ZIPSTREAM_INFLATING;
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::InflateEnd() {

  if ( m_State != ZIPSTREAM_INFLATING ) return false;

  bool bRetVal = inflateEnd( &m_Stream ) == Z_OK;
  if ( bRetVal ) {

    m_State = ZIPSTREAM_RAW;
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::DeflateInit( int compressLevel, int memLevel ) {

  if ( m_State != ZIPSTREAM_RAW ) return false;

  bool bRetVal = deflateInit2( &m_Stream, compressLevel, Z_DEFLATED, -MAX_WBITS, memLevel, Z_DEFAULT_STRATEGY ) == Z_OK;
  if ( bRetVal ) {

    m_State = ZIPSTREAM_DEFLATING;
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::DeflateEnd( size_t *pCompressed ) {

  if ( m_State != ZIPSTREAM_DEFLATING ) return false;

  size_t nCompressed = 0;

  m_Stream.next_in  = Z_NULL;
  m_Stream.avail_in = Z_NULL;

  int deflateResult = Z_OK;
  do {

    uLong old_total_out = m_Stream.total_out;
	//LOGI ("DeflateEnd:Before Deflate availout = %d, total_out = %d\n", m_Stream.avail_out, m_Stream.total_out);
    deflateResult = deflate( &m_Stream, Z_FINISH );
    LOGI ("DeflateEnd: deflate() api output = %d\n",deflateResult);
    nCompressed += ( m_Stream.total_out - old_total_out );

    LOGI ("DeflateEnd: m_Stream.total_out= %ld, old_total_out= %ld, nCompressed = %d \n",m_Stream.total_out,old_total_out,nCompressed);
    //*** Write out any deflated data
    DWORD dwBytesWritten;
#ifndef ANDROID
    WriteFile( m_hFile, m_StreamBuf, m_Stream.next_out - m_StreamBuf, &dwBytesWritten, NULL );
	printf ("DeflateEnd:Writing %d bytes to stream successfully written %d bytes\n", (m_Stream.next_out - m_StreamBuf), dwBytesWritten);
#else
    if ((NULL != m_hFile) && (m_Stream.next_out != Z_NULL))
	{
		dwBytesWritten = fwrite (m_StreamBuf, 1, (m_Stream.next_out - m_StreamBuf), m_hFile);
		fflush (m_hFile);
		LOGI ("DeflateEnd:Writing %d bytes to stream successfully written %d bytes\n", (m_Stream.next_out - m_StreamBuf), dwBytesWritten);
	}
#endif

    m_Stream.next_out  = m_StreamBuf;
    m_Stream.avail_out = STREAMBUF_SIZE;
    } while ( deflateResult == Z_OK );

  int iDeflatEndStatus = deflateEnd( &m_Stream );
  LOGI ("DeflateEnd: iDeflatEndStatus = %d , m_State = %d", iDeflatEndStatus,m_State);

  bool bRetVal = false;
  if(iDeflatEndStatus == Z_OK)
  {
	  bRetVal = true;
  }

  if ( bRetVal ) {

    m_State = ZIPSTREAM_RAW;
    }

  if ( pCompressed ) *pCompressed = nCompressed;
  LOGI ("DeflateEnd: Return value = %d", bRetVal);
  return bRetVal;
  }


/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipStream::Read( void *lpBuffer, size_t nSize, size_t *pCompressed ) {

  DWORD nSizeRead = 0;
  DWORD nCompressed = 0;

  //*** Can't read in deflate mode
  switch ( m_State ) {
    
    case ZIPSTREAM_RAW:
#ifndef ANDROID
		ReadFile( m_hFile, lpBuffer, nSize, &nSizeRead, NULL );
#else
		nSizeRead = fread (lpBuffer, 1, nSize, m_hFile);
#endif
      nCompressed = nSizeRead;
      break;

    case ZIPSTREAM_INFLATING:

      m_Stream.next_out  = (Bytef *)lpBuffer;
      m_Stream.avail_out = nSize;

      while ( m_Stream.avail_out > 0 ) {

        if ( m_Stream.avail_in == 0 ) {

          //*** Make sure we have a stream buffer
          if ( m_StreamBuf == NULL ) m_StreamBuf = new unsigned char[ STREAMBUF_SIZE ];

          DWORD nBytesRead = 0;
#ifndef ANDROID
          if ( ReadFile( m_hFile, m_StreamBuf, STREAMBUF_SIZE, &nBytesRead, NULL ) ) {
#else
		  if ( (fread (m_StreamBuf, 1, STREAMBUF_SIZE, m_hFile))>0 ) {
#endif
            m_Stream.next_in  = m_StreamBuf;
            m_Stream.avail_in = nBytesRead;
            }
          else {

            //*** Error reading from the file
            break;
            }
          }

        uLong old_total_out = m_Stream.total_out;
        uLong old_total_in  = m_Stream.total_in;

        int inflateResult = inflate( &m_Stream, Z_SYNC_FLUSH );

        nSizeRead   += ( m_Stream.total_out - old_total_out );
        nCompressed += ( m_Stream.total_in  - old_total_in );

        //*** If inflate didn't return Z_OK, dont bother looping anymore
        //*** Either we hit the end of the compressed stream, or there was an error
        if ( inflateResult != Z_OK ) break;
        }
      break;
    }

  if ( pCompressed ) *pCompressed = nCompressed;
  return nSizeRead;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::ReadData( void *pStruct, size_t StructSize ) {

  return Read( pStruct, StructSize ) == StructSize;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::ReadString( char **pString, size_t StringSize ) {

  char *Data = new char[ StringSize + 1 ];

  bool bRetVal = ReadData( Data, StringSize );
  if ( bRetVal ) {
      
    //*** add the null terminator
    Data[ StringSize ] = '\0';

    *pString = Data;
    }
  else {

    delete[] Data;
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipStream::Write( const void *lpBuffer, size_t nSize, size_t *pCompressed ) {

  DWORD nSizeWritten = 0;
  DWORD nCompressed = 0;

  LOGI ("TZipStream::Write\n");
  switch ( m_State ) {

    case ZIPSTREAM_RAW:
#ifndef ANDROID
      WriteFile( m_hFile, lpBuffer, nSize, &nSizeWritten, NULL );
	  printf ("ZIPSTREAM_RAW:Writing %d bytes to stream successfully written %d bytes\n", nSize, nSizeWritten);
#else
		if (NULL != m_hFile)
		{
			nSizeWritten = fwrite (lpBuffer, 1, nSize, m_hFile);
		    LOGI ("ZIPSTREAM_RAW:Writing %d bytes to stream successfully written %d bytes\n", nSize, nSizeWritten);
			fflush(m_hFile);
		}
#endif
      nCompressed = nSizeWritten;
      break;

    case ZIPSTREAM_DEFLATING:

      m_Stream.next_in  = (Bytef *)lpBuffer;
      m_Stream.avail_in = nSize;
      LOGI("Stream::Write mStreamAvail = %x, %d\n", m_Stream.next_in, m_Stream.avail_in);

      //*** Make sure we have a stream buffer
      if ( m_StreamBuf == NULL ) {
        m_StreamBuf = new unsigned char[ STREAMBUF_SIZE ];
        m_Stream.next_out  = m_StreamBuf;
        m_Stream.avail_out = STREAMBUF_SIZE;
        }

      while ( m_Stream.avail_in > 0 ) {

        uLong old_total_out = m_Stream.total_out;
        uLong old_total_in = m_Stream.total_in;

        int deflateResult = Z_OK;
        deflateResult = deflate( &m_Stream, Z_NO_FLUSH );

        nSizeWritten += ( m_Stream.total_in  - old_total_in  );
        nCompressed  += ( m_Stream.total_out - old_total_out );

        if ( m_Stream.avail_out == 0 ) {

          //*** Write out any deflated data
          DWORD dwBytesWritten;
#ifndef ANDROID
          if (    m_Stream.next_out != Z_NULL
               && !WriteFile( m_hFile, m_StreamBuf, STREAMBUF_SIZE, &dwBytesWritten, NULL ) ) {

            break;
            }
		  if (m_Stream.next_out != Z_NULL)
			printf ("ZIPSTREAM_DEFLATING:Writing %d bytes to stream successfully written %d bytes\n", STREAMBUF_SIZE, dwBytesWritten);
#else
		  LOGI ("ZIPSTREAM_DEFLATING:m_hFile = %x, next_out = %x\n", m_hFile, m_Stream.next_out);
		  if ((NULL != m_hFile) && (m_Stream.next_out != Z_NULL))
		  {
			dwBytesWritten = fwrite (m_StreamBuf, 1, STREAMBUF_SIZE, m_hFile);
			fflush (m_hFile);
			if (dwBytesWritten <= 0)
			{
				LOGI ("ZIPSTREAM_DEFLATING: fwrite dwBytesWritten = %d\n", dwBytesWritten);
				break;
			}
 		    
			if (m_Stream.next_out != Z_NULL)
				LOGI ("ZIPSTREAM_DEFLATING:Writing %d bytes to stream successfully written %d bytes\n", STREAMBUF_SIZE, dwBytesWritten);

		  }
#endif

          m_Stream.next_out  = m_StreamBuf;
          m_Stream.avail_out = STREAMBUF_SIZE;
          }

        //LOGI ("Deflate called and nSizeWritten = %d, nCompressed = %d;Deflate returned %x\n", nSizeWritten, nCompressed, deflateResult);
       // LOGI ("Deflate called old total in:%d, total out:%d\n", old_total_in,  old_total_out);
		//LOGI ("Deflate called mStream:total in:%d, total out:%d, availout:%d\n", m_Stream.total_in,  m_Stream.total_out,  m_Stream.avail_out);

        //*** If inflate didn't return Z_OK, dont bother looping anymore
        //*** Either we hit the end of the compressed stream, or there was an error

        if ( deflateResult != Z_OK ) 
				break;
        }
      break;

    }

  if ( pCompressed ) *pCompressed = nCompressed;
  return nSizeWritten;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipStream::WriteData( void *pStruct, size_t StructSize ) {

  return Write( pStruct, StructSize ) == StructSize;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipArchive::TZipArchive() {

  m_lpComment     = NULL;
  m_pCurrentFile  = NULL;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipArchive::~TZipArchive() {

  Close();
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::IsDirty() const {

  bool bRetVal = m_DirOffset == (size_t)-1;

  for( TZipFileListIter i = GetIter(); !bRetVal && i; i++ ) {

    bRetVal = i.Pcurrent()->IsDirty();
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::Open( char *lpFileName, DWORD dwCreateDisposition ) {

  bool bRetVal = m_Stream.Open( lpFileName, dwCreateDisposition )
              && ReadCentralDir();

  if ( !bRetVal ) {
    m_Stream.Close();
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
void TZipArchive::Close() {

  if ( m_Stream.IsOpen() ) {

    //*** Make sure any open file is closed
    CloseFile();

    //*** Write out the central directory
    if ( IsDirty() ) {

      TZipFileListIter i = GetIter();

      //*** Update any local file header records that are dirty
      for(; i; i++ ) {
        
        TZipFile *pFile = i.Pcurrent();
        if ( pFile->IsDirty() ) {

          pFile->WriteLocalHeader( m_Stream );
          }
        }

      //*** Goto the start of the central directory
      if ( m_DirOffset != (size_t)-1 ) {
        m_Stream.Seek( m_DirOffset, FILE_BEGIN );
        }
      else {
        m_DirOffset = m_Stream.Seek( 0, FILE_END );
        }

      //*** Write out the central file header record for each file
      for( i.restart(); i; i++ ) {
        i.Pcurrent()->WriteCentralHeader( m_Stream );
        }

      //*** Write the end of central directory record
      WriteEndOfDir();
      }

    m_Stream.Close();
    }

  if ( m_lpComment ) {
    delete[] m_lpComment;
    m_lpComment = NULL;
    }
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::OpenFile( TZipFile *pFile ) {

  if ( m_pCurrentFile != NULL ) return false;

  TZipLocalFileHeaderRec FileHeader;
  bool bRetVal = pFile->VerifyLocalHeader( m_Stream, &FileHeader )
              && m_Stream.Seek( pFile->GetOffset( FileHeader ), FILE_BEGIN ) != -1
              && m_Stream.InflateInit();

  if ( bRetVal ) {

    m_pCurrentFile = pFile;
    m_CurrentLeft  = pFile->GetUncompressedSize();
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
TZipFile *TZipArchive::CreateFile( const char *lpFileName, const char *lpComment, int compressLevel, int memLevel ) {

  if ( m_pCurrentFile != NULL ) return NULL;

  TZipFileHeaderRec Header;

  Header.Signature              = CENTRAL_FILE_HEADER_MAGIC;
  Header.VersionMadeBy          = 20;
  Header.VersionNeededToExtract = 20;

  switch ( compressLevel ) {

    case Z_NO_COMPRESSION:
      Header.GeneralBitFlags   = 0;
      Header.CompressionMethod = 0;
      break;

    case Z_BEST_SPEED:
      Header.GeneralBitFlags   = 4;
      Header.CompressionMethod = Z_DEFLATED;
      break;

    case Z_BEST_SPEED+1:
      Header.GeneralBitFlags   = 6;
      Header.CompressionMethod = Z_DEFLATED;
      break;

    case Z_BEST_COMPRESSION-1:
    case Z_BEST_COMPRESSION:
      Header.GeneralBitFlags   = 2;
      Header.CompressionMethod = Z_DEFLATED;
      break;

    default:
      Header.GeneralBitFlags   = 0;
      Header.CompressionMethod = Z_DEFLATED;
      break;
    }

  Header.LastModifiedTime       = 0;
  Header.LastModifiedDate       = 0;
  Header.Crc32                  = 0;
  Header.CompressedSize         = 0;
  Header.UncompressedSize       = 0;
  Header.FileNameLen            = 0;
  Header.ExtraFieldLen          = 0;
  Header.FileCommentLen         = 0;
  Header.DiskStart              = 0;
  Header.InternalAttributes     = 0;
  Header.ExternalAttributes     = 0;
  Header.OffsetOfLocalHeader    = m_DirOffset != -1 ? m_DirOffset : m_Stream.GetSize();

  m_pCurrentFile = m_Files.addAtTail( TZipFile( Header, lpFileName, lpComment ) );
  if ( m_pCurrentFile != NULL ) {

    //*** Write out the local header into the stream
    m_pCurrentFile->WriteLocalHeader( m_Stream );

    //*** Initialize deflating
    m_Stream.DeflateInit( compressLevel, memLevel );

    //*** Centeral Directory has been overwritten (or never existed)
    //*** mark the offset so we know it no longer exists
    m_DirOffset = -1;
    }

  return m_pCurrentFile;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipArchive::ReadFile( void *lpBuffer, size_t nSize ) {

  if ( m_pCurrentFile == NULL ) return 0;

  if      ( m_CurrentLeft == 0 ) return 0;
  else if ( m_CurrentLeft < nSize ) nSize = m_CurrentLeft;

  size_t RetVal = m_Stream.Read( lpBuffer, nSize );

  m_CurrentLeft -= RetVal;

  return RetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipArchive::WriteFile( const void *lpBuffer, size_t nSize ) {

  if ( m_pCurrentFile == NULL ) return 0;

  return m_pCurrentFile->WriteFile( m_Stream, lpBuffer, nSize );
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
void TZipArchive::CloseFile() {

  if ( m_pCurrentFile == NULL ) return;

  switch ( m_Stream.GetState() ) {

    case ZIPSTREAM_INFLATING:
      m_Stream.InflateEnd();
      break;

    case ZIPSTREAM_DEFLATING:
      m_pCurrentFile->DeflateEnd( m_Stream );
      break;
    }

  if ( m_pCurrentFile->IsDirty() ) {
    m_pCurrentFile->WriteLocalHeader( m_Stream );
    }

  m_pCurrentFile = NULL;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::AppendDirectory( const char *lpBasePath, const char *lpFile, const char *lpZip, int compressLevel, int memLevel, BYTE byIsUnixMode )
{

    LOGI ("::AppendDirectory Entry\n basepath=%s\n file = %s\n zip = %s; byUnixMode = %d\n\n", lpBasePath, lpFile, lpZip, byIsUnixMode);
    char FileName[ MAX_PATH ];
    strcpy( FileName, lpFile );

    if (0 == byIsUnixMode){
        strcat( FileName, "\\*.*" );
    } else {
        strcat( FileName, "/*.*" );
    }

    size_t Size = strlen( lpBasePath );
    LOGI("Length of Base Path = %d", (int)Size);
    if ( strcncmp( FileName, lpBasePath, Size ) == 0 ) {
      
      memmove( FileName, FileName + Size, ( strlen( FileName ) + 1 - Size )*sizeof(char) );
    }
  

    char ZipName[ MAX_PATH ];
    strcpy( ZipName, lpZip != NULL ? lpZip : lpFile );
    if (0 == byIsUnixMode){
      strcat( ZipName, "\\" );
    } else {
      strcat( ZipName, "/" );
    }

    LOGI("::AppendDirectory calling Append Files with basepath=%s, file = %s, zip = %s\n", lpBasePath, FileName, ZipName);
    //return AppendFiles( lpBasePath, FileName, ZipName, compressLevel, memLevel );
    return true;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::AppendFile( const char* lpFilePath, const char *lpZipName, const char *lpComment, int compressLevel, int memLevel ) {

  //USES_CONVERSION;

  bool bRetVal = false;
  char *pszZipName = NULL;
  char *pszComment = NULL;
  int nLen = strlen(lpFilePath);

  LOGI ("::AppendFile filepath=%s, zipname = %s, comment = %s\n", lpFilePath, lpZipName, lpComment);

#ifndef ANDROID
  TCHAR stFileName[MAX_PATH];
  _stprintf(stFileName, _T("%hs"), lpFilePath); 
  HANDLE hFile = ::CreateFile(
    stFileName,
    GENERIC_READ,
    FILE_SHARE_READ,
    NULL,
    OPEN_EXISTING,
    FILE_ATTRIBUTE_NORMAL,
    NULL );
	if ( hFile != INVALID_HANDLE_VALUE ) 
	{
#else
  FILE *hFile = fopen (lpFilePath, "rb");
  if (NULL != hFile) 
  {
#endif
	  
	if ( CreateFile( lpZipName, lpComment, compressLevel, memLevel ) ) {

		LOGI ("::AppendFile Creating a file has successful %d\n",1);

      char Buffer[ READ_BUFFER_SIZE ];
      DWORD BytesRead;
	  DWORD dwTotalBytes = 0;
#ifndef ANDROID

      while ( ::ReadFile( hFile, Buffer, sizeof(Buffer), &BytesRead, NULL ) && BytesRead > 0 ) {
		printf ("::AppendFile Writing Chunk %d bytes written\n", BytesRead);
		dwTotalBytes += BytesRead;
		WriteFile( Buffer, BytesRead );
	  }
	  LOGI ("File Size = %d; Total Bytes dumped in File = %d\n", GetFileSize( hFile, NULL ), dwTotalBytes);
#else
	  fseek (hFile, 0, SEEK_END);
	  int nFileSize = ftell (hFile);
	  rewind(hFile);

	  int nBytesRead = ftell (hFile);
	  int nPrevRead = 0;

	  while (nBytesRead < nFileSize)
	  {
		  nPrevRead = nBytesRead;
		  fread (Buffer, 1, sizeof(Buffer), hFile);
		  nBytesRead = ftell(hFile);

		  if ((nBytesRead-nPrevRead)>0)
		  {
			LOGI ("::AppendFile Writing Chunk %d bytes written\n", (nBytesRead-nPrevRead));
			size_t size = WriteFile( Buffer, nBytesRead-nPrevRead );
			bRetVal = false;
			if(size > 0)
			{
				bRetVal = true;
			}
			LOGI ("::AppendFile Write file func returned value = %d , size = %d\n", bRetVal, size);
			if(false == bRetVal)
			{
				break;
			}
		  }
	  }

	  LOGI ("File Size = %d; Total Bytes dumped in File = %d\n", nFileSize, nBytesRead);

#endif
      CloseFile();
    }
	else
	{
		LOGI ("::AppendFile Creating a file has failed %d\n",bRetVal);
	}
#ifndef ANDROID
    CloseHandle( hFile );
#else
	  if (NULL != hFile) {
		   fflush (hFile);
		   fclose (hFile);
	  }
#endif
    }

  return bRetVal;
  }
    
/******************************************************************************
 * Desc:
 *   In:
 *  Out:
*****************************************************************************/
bool TZipArchive::AppendFiles( const char *lpBasePath, const char *lpZip, int compressLevel, int memLevel , BYTE byIsUnixMode) {
    bool bRet = false;
    char szFileNameToRecurse[ 512 ];
    char szZipFilePath[512];
    DWORD dwError = 0;
    bool bFound = false;
    DIR *pDir = NULL;
    dirent *pEntry = NULL;

    LOGI ("AppendFiles::Entry with lpBase = %s, lpZip = %s\n", lpBasePath, lpZip);
        
    do {
        if (NULL == lpBasePath || 0 == strlen(lpBasePath)) {
            LOGI("\nNULL passed as base path\n");
            break;
        }
            
        pDir = opendir(lpBasePath);
        
        LOGI("OpenDir called returned pDir = %x\n", pDir);

        if (NULL == pDir) {
            LOGI("\n pDir NULL so breaking");
            break;
        }
            
        while ((pEntry = readdir(pDir)) != NULL) {
            LOGI("ZA:: entry received: %s, name_start_char = %c, type = %d\n", pEntry->d_name,pEntry->d_name[0], pEntry->d_type);
            if ((0x04 == (int) pEntry->d_type) && (pEntry->d_name[0] != '.'))
            {
                // The current path is a valid directory hence include for zip
                strcpy(szFileNameToRecurse, lpBasePath);
                strcpy(szZipFilePath, lpZip);
                strcat ( szFileNameToRecurse, "/");
                strcat ( szFileNameToRecurse, pEntry->d_name);
                if (strlen(szZipFilePath)!=0)
                    strcat ( szZipFilePath, "/");
                strcat ( szZipFilePath,  pEntry->d_name );
                
                LOGI("\n ZA::AppendDirec called with basepath:%s\n ZipName=%s\n",szFileNameToRecurse, szZipFilePath);
                bRet = AppendFiles( szFileNameToRecurse, szZipFilePath, compressLevel, memLevel, byIsUnixMode );
                LOGI("AppendDirec returned %d\n", bRet);
                if(false == bRet)
                {
                  	break;
                }
            }
            else if ((0x04 != (int) pEntry->d_type) && (pEntry->d_name[0] != '.'))
            {
                strcpy(szFileNameToRecurse, lpBasePath);
                strcpy(szZipFilePath, lpZip);
                strcat ( szFileNameToRecurse, "/");
                strcat ( szFileNameToRecurse, pEntry->d_name);
                if (strlen(szZipFilePath)!=0)
                    strcat ( szZipFilePath, "/");
                strcat ( szZipFilePath,  pEntry->d_name );
                    
                LOGI("\n ZA::AppendFile called with FileName:%s\n ZipName: %s\n",szFileNameToRecurse, szZipFilePath);
                bRet = AppendFile( szFileNameToRecurse, szZipFilePath, NULL, compressLevel, memLevel );
                LOGI("AppendFile returned %d\n", bRet);
                if(false == bRet)
                {
                	break;
                }
            }
        }
            
        LOGI("ZA::Closing Dir\n");
        closedir(pDir);
            
    } while (FALSE);
    LOGI ("AppendFiles::Exit");
    
    return bRet;
}

#if 0
/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::AppendFiles( const char*lpBasePath, const char *lpFile, const char *lpZip, int compressLevel, int memLevel , BYTE byIsUnixMode) {

  bool bRetVal = true;

  char FileName[ MAX_PATH ];
  strcpy( FileName, lpBasePath );
  AppendFilePath( FileName, lpFile, byIsUnixMode);

  char ZipName[ MAX_PATH ];
  strcpy( ZipName, lpZip != NULL ? lpZip : lpFile );
  LOGI("AppendFiles:: Entry FileName = %s\n ZipPath = %s\n\n", FileName, ZipName);

  char *lpFileName = FindFileName( FileName , byIsUnixMode);
  char *lpZipName  = FindFileName( ZipName, byIsUnixMode );

#ifndef ANDROID_NDK
  WIN32_FIND_DATA fd;
  TCHAR tchFileName[MAX_PATH];
  char tchFindFile[MAX_PATH];
  _stprintf(tchFileName, _T("%hs"), FileName); 
  HANDLE hFind = FindFirstFile( tchFileName, &fd );
    
    printf("ZA::basepath: %s\n FileName: %s\n ZipName:%s\n\n",lpBasePath, lpFile, lpZip);
    printf("ZA::lpFileName: %s\n lpZipName:%s\n\n", lpFileName, lpZipName);

  if ( hFind != INVALID_HANDLE_VALUE ) {
    do {

      if ( fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY && fd.cFileName[0] != '.') {
		
		sprintf (tchFindFile, "%ls", fd.cFileName);
        strcpy( lpFileName, tchFindFile );
        strcpy( lpZipName,  tchFindFile );
		
		printf ("\n::AppendFiles appending dir %ls, %ls, %ls\n", lpBasePath, FileName, ZipName);

        bRetVal &= AppendDirectory( lpBasePath, FileName, ZipName, compressLevel, memLevel, byIsUnixMode );
        }
      else if ( !(fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) ) {

		sprintf (tchFindFile, "%ls", fd.cFileName);
        strcpy( lpFileName, tchFindFile );
        strcpy( lpZipName,  tchFindFile );

		printf ("\n::AppendFile appending file %ls, %ls, %ls\n", lpBasePath, FileName, ZipName);
        bRetVal &= AppendFile( FileName, ZipName, NULL, compressLevel, memLevel );
        }
      } while ( FindNextFile( hFind, &fd ) );

    FindClose( hFind );
    }
#else
  	DWORD dwError = 0;
	BYTE byPkgID = 0;
	bool bFound = false;
	DIR *pDir = NULL;
	dirent *pEntry = NULL;
	String *pDirToRecurse = NULL;
	String *pszRootPath = NULL;
    
	do
	{
		if (NULL == lpBasePath) {
			LOGI("\nNULL passed as base path\n");
			break;
		}
        
		LOGI("ZA::basepath: %s\n FileName: %s\n ZipName:%s\n\n",lpBasePath, lpFile, lpZip);
		LOGI("ZA::lpFileName: %s\n lpZipName:%s\n\n", lpFileName, lpZipName);
		
		pDir = opendir(lpBasePath);
        
        
		if (NULL == pDir) {
			break;
			LOGI("\n pDir NULL so breaking");
		}
        
		while ((pEntry = readdir(pDir)) != NULL) {
			if ((0x04 == (int) pEntry->d_type) && (pEntry->d_name[0] != '.'))
            {
                LOGI("ZA::Directory entry received: %s\n", pEntry->d_name);
                     
                // The current path is a valid directory hence include for zip
                strcpy( lpFileName, pEntry->d_name );
                strcpy( lpZipName,  pEntry->d_name );
                     
                LOGI("\n ZA::AppendDirectory called with basepath:%s\n, FileName: %s\n, ZipName=%s\n",lpBasePath, FileName, ZipName);
                bRetVal &= AppendDirectory( lpBasePath, FileName, ZipName, compressLevel, memLevel, byIsUnixMode );
            }
            else if (0x04 != (int) pEntry->d_type)
            {
                LOGI("ZA::File entry received: %s\n", pEntry->d_name);
                strcpy( lpFileName, pEntry->d_name );
                strcpy( lpZipName,  pEntry->d_name );
                
                LOGI("\n ZA::AppendFile called with FileName:%s, ZipName: %s\n",FileName, ZipName);
                bRetVal &= AppendFile( FileName, ZipName, NULL, compressLevel, memLevel );
            }
        }
                          
        LOGI("ZA::Closing Dir\n");
        closedir(pDir);
    } while (FALSE);
#endif
    LOGI("AppendFiles:: Exit\n\n");
  return bRetVal;
}
#endif
    
/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
size_t TZipArchive::FindEndOfDir() {

  //*** Seek to the start of the end of central directory record (assuming no global comment)
  size_t nPosition = m_Stream.Seek( -(long)sizeof(TZipEndOfCentralDirRec), FILE_END );
  if ( nPosition == -1 ) return -1;

  char Buffer[ READ_BUFFER_SIZE ];
  if ( !m_Stream.Read( Buffer, 4 ) ) return -1;

  //*** If the signature matches, then we've done here!
  if ( *(unsigned long *)Buffer == END_OF_CENTRAL_DIRECTORY_MAGIC ) {
    return nPosition;
    }

  //*** Ok, either this isn't a zip or theres a comment at the end of the zip
  //*** Search backwards from our current position up to 65535 bytes away (maximum size of the comment)

  size_t nMinPosition = 0;

  if ( nPosition > MAX_COMMENT ) {
    nMinPosition = nPosition - MAX_COMMENT;
    }

  while ( nPosition > nMinPosition ) {

    size_t nNumRead;
    if ( nMinPosition + READ_BUFFER_SIZE > nPosition ) {
      nNumRead = nPosition - nMinPosition;
      }
    else {
      nNumRead = READ_BUFFER_SIZE;
      }

    nPosition -= nNumRead;

    //*** Seek to our new search position
    if (     m_Stream.Seek( nPosition, FILE_BEGIN ) == -1
         || !m_Stream.ReadData( Buffer, nNumRead ) ) return -1;

    for( unsigned long i = 0; i < nNumRead - 3; i++ ) {

      if ( *((unsigned long *)(Buffer+i)) == END_OF_CENTRAL_DIRECTORY_MAGIC ) {
    
        return nPosition + i;
        }
      }
    }

  return -1;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
bool TZipArchive::ReadCentralDir() {

  bool bRetVal = true;

  if ( m_Stream.GetSize() > 0 ) {

    //*** All existing zips should/must have a central directory
    TZipEndOfCentralDirRec Rec;
    size_t EndOfDirPos = FindEndOfDir();
    if (    EndOfDirPos < (size_t)-1
         && m_Stream.Seek( EndOfDirPos, FILE_BEGIN ) != -1
         && m_Stream.ReadData( &Rec, sizeof(Rec) )
         && Rec.CurrentDisk == 0
         && Rec.NumDisks == 0
         && Rec.NumEntries == Rec.TotalEntries
         && EndOfDirPos >= Rec.Offset + Rec.Size
         && ( Rec.CommentSize == 0 || m_Stream.ReadString( &m_lpComment, Rec.CommentSize ) )
         && m_Stream.Seek( Rec.Offset, FILE_BEGIN ) != -1 ) {

      m_DirOffset = Rec.Offset;

      for( unsigned short i = 0; bRetVal && i < Rec.NumEntries; i++ ) {
        bRetVal = m_Files.addAtTail( TZipFile() )->ReadCentralHeader( m_Stream );
        }
      }
    else {

      bRetVal = false;
      }
    }
  else {

    //*** Indicates that there is no central directory
    m_DirOffset = -1;
    }

  return bRetVal;
  }

/******************************************************************************
 * Desc:
 *   In:
 *  Out:
 *****************************************************************************/
void TZipArchive::WriteEndOfDir() {

  size_t EndOfDirPos = m_Stream.Seek( 0, FILE_CURRENT );

  TZipEndOfCentralDirRec Rec;
  Rec.Signature    = END_OF_CENTRAL_DIRECTORY_MAGIC;
  Rec.CurrentDisk  = 0;
  Rec.NumDisks     = 0;
  Rec.NumEntries   = (unsigned short)min( 0xFFFF, m_Files.numItems() );
  Rec.TotalEntries = Rec.NumEntries;
  Rec.Size         = EndOfDirPos - m_DirOffset;
  Rec.Offset       = m_DirOffset;
  Rec.CommentSize  = m_lpComment != NULL ? strlen( m_lpComment ) : 0;

  m_Stream.WriteData( &Rec, sizeof(Rec) );

  if ( m_lpComment != NULL ) {
    m_Stream.WriteData( m_lpComment, Rec.CommentSize );
    }
  }

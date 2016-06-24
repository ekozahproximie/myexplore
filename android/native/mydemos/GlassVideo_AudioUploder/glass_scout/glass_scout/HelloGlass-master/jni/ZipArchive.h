/*****************************************************************************\
**                               Copyright 1992                              **
**                            CTN Data Service, Inc.                         **
**                             All Rights Reserved                           **
**                                                                           **
** Project: Farm Works                                                       **
**                                                                           **
** File: ZipArchive.h                                                        **
**                                                                           **
** Authors:  Lowell D. Smith                                                 **
**                                                                           **
** Date: 08/31/2007                                                          **
**                                                                           **
** Description:                                                              **
**                                                                           **
** Modification record:                                                      **
** --Date-- WHO --- Reason ---                                               **
**                                                                           **
\*****************************************************************************/

/******************************************************************************
 * Zip internal structures
 * NOTE: Please dont modifiy these without carefully understanding of the
 *       .ZIP File Format Specification.
 * See http://www.pkware.com/documents/casestudies/APPNOTE.TXT
 ******************************************************************************/

#include "CTNList.h"
#include "zlib.h"

#pragma pack(push,1)

//*** Default memory level for deflate (as given by zlib's manual)
#define DEF_MEM_LEVEL 8
#define FILE_BEGIN           0
#define FILE_CURRENT         1
#define FILE_END             2
/******************************************************************************
 * Handy short cuts
 ******************************************************************************/
typedef unsigned int   uint;
typedef unsigned short ushort;

/******************************************************************************
 * TZipLocalFileHeaderRec
 ******************************************************************************/
struct TZipLocalFileHeaderRec {

  uint   Signature;
  ushort VersionNeededToExtract;
  ushort GeneralBitFlags;
  ushort CompressionMethod;
  ushort LastModifiedTime;
  ushort LastModifiedDate;
  uint   Crc32;
  uint   CompressedSize;
  uint   UncompressedSize;
  ushort FileNameLen;
  ushort ExtraFieldLen;
  };

/******************************************************************************
 * TZipFileHeaderRec
 ******************************************************************************/
struct TZipFileHeaderRec {

  uint   Signature;
  ushort VersionMadeBy;
  ushort VersionNeededToExtract;
  ushort GeneralBitFlags;
  ushort CompressionMethod;
  ushort LastModifiedTime;
  ushort LastModifiedDate;
  uint   Crc32;
  uint   CompressedSize;
  uint   UncompressedSize;
  ushort FileNameLen;
  ushort ExtraFieldLen;
  ushort FileCommentLen;
  ushort DiskStart;
  ushort InternalAttributes;
  uint   ExternalAttributes;
  uint   OffsetOfLocalHeader;
  };

/******************************************************************************
 * TZipEndOfCentralDirRec
 ******************************************************************************/
struct TZipEndOfCentralDirRec {

  uint   Signature;
  ushort CurrentDisk;
  ushort NumDisks;
  ushort NumEntries;
  ushort TotalEntries;
  uint   Size;
  uint   Offset;
  ushort CommentSize;
  };

#pragma pack(pop)

/******************************************************************************
 * Stream State
 ******************************************************************************/
#define ZIPSTREAM_RAW       0
#define ZIPSTREAM_INFLATING 1
#define ZIPSTREAM_DEFLATING 2

/******************************************************************************
 * TZipStream
 ******************************************************************************/
class TZipStream {

  char *m_lpFileName;
#ifndef ANDROID
  HANDLE m_hFile;
#else
  FILE *m_hFile;
#endif

  int m_State;
  z_stream m_Stream;
  unsigned char *m_StreamBuf;

public:

  TZipStream();
  ~TZipStream();

#ifndef ANDROID
  bool    IsOpen() const      { return m_hFile != INVALID_HANDLE_VALUE; }
  char* GetFileName() const { return m_lpFileName; }
  size_t  GetSize() const     { DWORD dwSize = GetFileSize( m_hFile, NULL );  printf ("Size of stream = %d\n", dwSize); return dwSize; }
  int     GetState() const    { return m_State; }
#else
  bool    IsOpen() const      { return m_hFile != NULL; }
  char* GetFileName() const { return m_lpFileName; }

  size_t  GetSize() const     
  { 
	  if (NULL != m_hFile)
	  {
		  DWORD dwCurptr = ftell (m_hFile);
		  fseek (m_hFile, 0, SEEK_END);
		  DWORD dwEnd = ftell (m_hFile);
		  fseek (m_hFile, dwCurptr, SEEK_SET);
		  printf ("Size of stream = %d\n", dwEnd);
		  return dwEnd; 
	  }
	  return 0;
  }
  int     GetState() const    { return m_State; }
#endif

  bool Open( char *lpFileName, DWORD dwCreateDisposition );
  void Close();

  bool DeflateInit( int compressLevel, int memLevel );
  bool DeflateEnd( size_t *pCompressed = NULL );

  bool InflateInit();
  bool InflateEnd();

  size_t Seek( long Distance, long Method );
  size_t Read( void *lpBuffer, size_t nSize, size_t *pCompressed = NULL );
  size_t Write( const void *lpBuffer, size_t nSize, size_t *pCompressed = NULL );

  bool ReadData( void *pStruct, size_t StructSize ); 
  bool ReadString( char **pString, size_t StringSize );

  bool WriteData( void *pStruct, size_t StructSize );
  };

/******************************************************************************
 * TZipFile
 ******************************************************************************/
class TZipFile {

  TZipFileHeaderRec m_Header;
  char *m_lpFileName;
  char *m_lpComment;
  bool  m_bDirty;

  bool GetLocalHeader( TZipLocalFileHeaderRec *pLocalHeader ) const;
  bool CompareLocalHeader( const TZipLocalFileHeaderRec &FileHeader ) const;

public:

  TZipFile();
  TZipFile( const TZipFileHeaderRec &Header, const char *lpFileName, const char *lpComment );
  TZipFile( const TZipFile &A );
  ~TZipFile();

  bool   operator ==( const TZipFile &A ) const;

  bool   IsDirty() const              { return m_bDirty; }
  bool   IsFolder() const             { return m_lpFileName[ strlen( m_lpFileName ) - 1 ] == '\\'; }
  const char* GetComment() const      { return m_lpComment;  }
  int    GetCompressionMethod() const { return m_Header.CompressionMethod != 0; }
  size_t GetCompressedSize() const    { return m_Header.CompressedSize; }
  const char* GetFileName() const     { return m_lpFileName; }
  size_t GetUncompressedSize() const  { return m_Header.UncompressedSize; }
  size_t GetOffset( const TZipLocalFileHeaderRec &FileHeader ) const { return m_Header.OffsetOfLocalHeader + sizeof(FileHeader) + FileHeader.FileNameLen + FileHeader.ExtraFieldLen; }
  uint   GetCrc32() const             { return m_Header.Crc32; }

  void Clear();
  bool ReadCentralHeader( TZipStream &Stream );
  bool WriteCentralHeader( TZipStream &Stream );
  bool WriteLocalHeader( TZipStream &Stream );
  bool VerifyLocalHeader( TZipStream &Stream, TZipLocalFileHeaderRec *pFileHeader );

  size_t WriteFile( TZipStream &Stream, const void *lpBuffer, size_t nSize );
  bool DeflateEnd( TZipStream &Stream );
  };

/******************************************************************************
 * TZipFileHeaderList
 ******************************************************************************/
typedef CTN_List< TZipFile >     TZipFileList;
typedef CTN_ListIter< TZipFile > TZipFileListIter;

/******************************************************************************
 * TZipArchive
 *****************************************************************************/
class TZipArchive {

  TZipStream   m_Stream;
  TZipFileList m_Files;
  size_t       m_DirOffset;
  char		   *m_lpComment;
  TZipFile    *m_pCurrentFile;
  size_t       m_CurrentLeft;

  size_t FindEndOfDir();
  bool ReadCentralDir();
  void WriteEndOfDir();

public:

  TZipArchive();
  ~TZipArchive();

  bool IsDirty() const;
  char* GetFileName() const      { return m_Stream.GetFileName(); }
  TZipFileListIter GetIter() const { return TZipFileListIter( m_Files ); }

  bool Open( char *lpFileName, DWORD dwCreateDisposition = 3 );
  void Close();

  bool      OpenFile( TZipFile *pFile );
  TZipFile *CreateFile( const char *lpFileName, const char *lpComment, int compressLevel = Z_DEFAULT_COMPRESSION, int memLevel = DEF_MEM_LEVEL );
  size_t    ReadFile( void *lpBuffer, size_t nSize );
  size_t    WriteFile( const void *lpBuffer, size_t nSize );
  void      CloseFile();

  bool   AppendDirectory( const char *lpBasePath, const char *lpDirPath, const char *lpZipName, int compressLevel = Z_DEFAULT_COMPRESSION, int memLevel = DEF_MEM_LEVEL, BYTE byIsUnixMode = 1 );
  bool   AppendFile( const char *lpFilePath, const char *lpZipName, const char *lpComment, int compressLevel = Z_DEFAULT_COMPRESSION, int memLevel = DEF_MEM_LEVEL );
  bool   AppendFiles( const char *lpBasePath, const char *lpZipName, int compressLevel = Z_DEFAULT_COMPRESSION, int memLevel = DEF_MEM_LEVEL, BYTE byIsUnixMode = 1 );

  };
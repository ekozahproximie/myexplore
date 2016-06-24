/*****************************************************************************\
**                               Copyright 1992                              **
**                            CTN Data Service, Inc.                         **
**                             All Rights Reserved                           **
**                                                                           **
** Project: Farm Works                                                       **
**                                                                           **
** File: CTNList.h                                                           **
**                                                                           **
** Authors: Anthony J. Bowers                                                **
**                                                                           **
** Date: 09/25/1993                                                          **
**                                                                           **
** Description: Template classes for the implementation of a                 **
**              doublely linked	list.                                        **
**                                                                           **
** Modification record:                                                      **
** --Date-- WHO --- Reason ---                                               **
**                                                                           **
\*****************************************************************************/

#ifndef __CTNLIST_H
#define __CTNLIST_H

template <class T> class CTN_List;
template <class T> class CTN_ListIter;

//////////////////////////////////////////////////////////////////////
//
//////////////////////////////////////////////////////////////////////
template < class T >
void swap( T &a, T &b ) {

  T t( a );
  a = b;
  b = t;
  }

/***********************************************************************
 * CTN_ListNode: Template class for a node in the list.
 ***********************************************************************/
template <class T> class CTN_ListNode {

  T              Data;
  CTN_ListNode  *pNext;
  CTN_ListNode	*pPrev;

public:

  //*** Constructor
  CTN_ListNode( const T &t ) : Data(t),
                               pNext(NULL),
                               pPrev(NULL) {

    }

  friend class CTN_List<T>;
  friend class CTN_ListIter<T>;
  };

/***********************************************************************
 * CTN_List
 ***********************************************************************/
template <class T> class CTN_List {

  CTN_ListNode<T>    *pHead;
  CTN_ListNode<T>    *pTail;
  long                NumItems;

  //*** Find
  CTN_ListNode<T> *Find( const T &t) {

    CTN_ListNode<T> *pTmp = pHead;
    bool            Found = false;

    while ( pTmp && !Found ) {
      if ( pTmp->Data == t )
        Found = true;
      else
        pTmp = pTmp->pNext;
      }

    return pTmp;
    }

public:

  //*** Constructor
  CTN_List()
    : pHead( NULL ),
      pTail( NULL ),
      NumItems( 0 )  {
    }

  //*** Copy Constructor
  CTN_List( const CTN_List &A )
    : pHead( NULL ),
      pTail( NULL ),
      NumItems( 0 )  {
    
    *this = A;
    }

  //*** Destructor
  virtual ~CTN_List() {

    flush();
    }

  //*** operator =  
  CTN_List &operator = ( const CTN_List<T> &A ) {

    CTN_ListNode<T>  *pCur = A.pHead;

    flush();

    while ( pCur ) {
      addAtTail( pCur->Data );
      pCur = pCur->pNext;
      }

    return *this;
    }

  //*** add  
  T *add( const T &t ) {

    NumItems++;
    CTN_ListNode<T> *pNewNode = new CTN_ListNode<T>( t );
    if ( pHead )
      pHead->pPrev = pNewNode;
    pNewNode->pNext = pHead;
    pHead = pNewNode;

    if ( !pTail )
      pTail = pNewNode;

    return &(pNewNode->Data);
    }

  //*** addAfter
  T *addAfter( const T &t, const T &After ) {

    CTN_ListNode<T> *pTmp = Find( After );
    if ( !pTmp ) return NULL;

    NumItems++;
    CTN_ListNode<T> *pNewNode = new CTN_ListNode<T>( t );

    pNewNode->pNext = pTmp->pNext;
    pNewNode->pPrev = pTmp;

    pTmp->pNext = pNewNode;
    if (pNewNode->pNext)
      pNewNode->pNext->pPrev = pNewNode;
    else
      pTail = pNewNode;
    return &(pNewNode->Data);
    }

  //*** addBefore  
  T *addBefore( const T &t, const T &Before ) {

    CTN_ListNode<T> *pTmp = Find( Before );
    if ( !pTmp ) return NULL;

    NumItems++;
    CTN_ListNode<T> *pNewNode = new CTN_ListNode<T>( t );

    pNewNode->pPrev = pTmp->pPrev;
    pNewNode->pNext = pTmp;

    pTmp->pPrev = pNewNode;
    if (pNewNode->pPrev)
      pNewNode->pPrev->pNext = pNewNode;
    else
      pHead = pNewNode;
    return &(pNewNode->Data);
    }

  //*** addAtTail 
  T *addAtTail( const T &t ) {

    NumItems++;
    CTN_ListNode<T> *pNewNode = new CTN_ListNode<T>( t );

    if ( pTail )
      pTail->pNext = pNewNode;
    pNewNode->pPrev = pTail;
    pTail = pNewNode;

    if ( !pHead )
      pHead = pNewNode;

    return &(pNewNode->Data);
    }

  //*** PopHead
  bool PopHead( T *pRetVal = NULL ) {

    if ( pHead == NULL ) return false;

    if ( pRetVal ) {
      *pRetVal = pHead->Data;
      }

    removeHead();
    return true;
    }

  //*** PopTail  
  bool PopTail( T *pRetVal = NULL ) {
  
    if ( pTail == NULL ) return false;

    if ( pRetVal ) {
      *pRetVal = pTail->Data;
      }

    removeTail();
    return true;
    }

  //*** detach  
  void detach( const T &t ) {

    CTN_ListNode<T> *pTmp = Find( t );
    if ( !pTmp ) return;

    NumItems--;

    if ( pTmp == pHead )
      pHead = pHead->pNext;
    if ( pTmp == pTail )
      pTail = pTail->pPrev;

    if ( pTmp->pPrev)
      pTmp->pPrev->pNext = pTmp->pNext;
    if ( pTmp->pNext )
      pTmp->pNext->pPrev = pTmp->pPrev;

    delete pTmp;
    }

  //*** numItems
  long numItems() const {

    return NumItems;
    }

  //*** flush  
  void flush() {

    CTN_ListNode<T> *pTmp;

    while ( pHead ) {
      pTmp = pHead->pNext;
      delete pHead;
      pHead = pTmp;
      }

    pTail = NULL;

    NumItems = 0;
/*
    if ( CTN_ListNode<T>::pMemPool ) {
      unsigned long Size     = CTN_ListNode<T>::pMemPool->Size();
      unsigned long UseCount = CTN_ListNode<T>::pMemPool->UseCount();

      //*** Shink if size and usecount is less than half the space
      if ( Size && (UseCount < (Size /2)) )
        CTN_ListNode<T>::pMemPool->Shrink();
      }
*/
    }

  //*** isEmpty
  bool isEmpty() {
  
    return ( pHead == NULL );
    }

  //*** hasMember
  bool hasMember( const T &t ) {

    return Find( t ) != NULL;
    }

  //*** findMember
  bool findMember( T &t ) {

    CTN_ListNode<T> *pItem = Find( t );
    if (pItem) {
      t = pItem->Data;
      return true;
      }
    return false;
    }

  //*** PointerToMember
  T *PointerToMember( const T &t ) {
  
    CTN_ListNode<T> *pItem = Find( t );
    if ( pItem )
      return &(pItem->Data);

    return NULL;
    }

  //*** peekHead  
  //T *peekHead() {
  //  return pHead ? &pHead->Data : NULL;
  //  }

  //*** peekTail  
  //T *peekTail() {
  //  return pTail ? &pTail->Data : NULL;
  //  }

  //*** peekHead  
  T *PointerToHead() {
    return pHead ? &pHead->Data : NULL;
    }

  //*** peekTail  
  T *PointerToTail() {
    return pTail ? &pTail->Data : NULL;
    }

  //*** removeHead  
  void removeHead() {

    if ( pHead ) {
      NumItems--;
      CTN_ListNode<T> *pTmp = pHead;
      pHead = pHead->pNext;
      if ( pHead )
        pHead->pPrev = NULL;
      else
        pTail = NULL;
      delete pTmp;
      }
    }

  //*** removeTail  
  void removeTail() {
  
    if ( pTail ) {
      NumItems--;
      CTN_ListNode<T> *pTmp = pTail;
      pTail = pTail->pPrev;
      if ( pTail )
        pTail->pNext = NULL;
      else
        pHead = NULL;
      delete pTmp;
      }
    }

  T *operator [] ( const int n ) {

    if ( n < 0 && n >= NumItems ) {
      return NULL;
      }
    else {
      CTN_ListNode<T> *pItem = pHead;
      for ( int i = 0; i < n; i++ ) {
        pItem = pItem->pNext;
        }
      return &(pItem->Data);
      }
    }

  void Swap( CTN_List<T> &_Other ) {

    swap( pHead, _Other.pHead );
    swap( pTail, _Other.pTail );
    swap( NumItems, _Other.NumItems );
    }

  friend class CTN_ListIter<T>;
  };

/*****************************************************************
 * CTN_ListIter -- Template class for an iterator for the tree.
 ******************************************************************/
template <class T> class CTN_ListIter {

  CTN_ListNode<T>   *pCur;
  const CTN_List<T> *pList;

public:

  //*** Constructor
  CTN_ListIter( const CTN_List<T> &List ) {

    pList = &List;
    pCur = pList->pHead;
    }

  //*** restart
  void restart() {

    pCur = pList->pHead;
    }

  //*** restart (at tail)
  void restart( const bool ) {

    pCur = pList->pTail;
    }

  //*** current
  T current() {

    return pCur->Data;
    }

  //*** Pcurrent
  T *Pcurrent() const {

    return &(pCur->Data);
    }

  //*** Pprev
  T *Pprev() {
    if ( pCur->pPrev )
      return &(pCur->pPrev->Data);
    else
      return NULL;
    }

  //*** Pnext
  T *Pnext() {
    if ( pCur->pNext )
      return &(pCur->pNext->Data);
    else
      return NULL;
    }

  //*** operator ++ (A++)
  CTN_ListIter<T> operator ++ ( int ) {

    CTN_ListIter<T> Temp = *this;
    if ( pCur ) pCur = pCur->pNext;
    return Temp;
    }

  //*** operator ++ (++A)
  CTN_ListIter<T> &operator ++ () {

    if ( pCur ) pCur = pCur->pNext;
    return *this;
    }

  //*** operator -- (A--)
  CTN_ListIter<T> operator -- ( int ) {

    CTN_ListIter<T> Temp = *this;
    if ( pCur ) pCur = pCur->pPrev;
    return Temp;
    }

  //*** operator -- (--A)
  CTN_ListIter<T> &operator -- () {

    if ( pCur ) pCur = pCur->pPrev;
    return *this;
    }

  //*** operator int
  operator int() {

    return ( pCur != NULL );
    }
  };

#endif


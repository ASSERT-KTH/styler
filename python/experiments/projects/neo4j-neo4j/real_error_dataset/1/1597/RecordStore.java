/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.store;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.neo4j.internal.helpers.collection.Visitor;
import org.neo4j.internal.id.IdSequence;
import org.neo4j.io.pagecache.PageCursor;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.kernel.impl.store.record.AbstractBaseRecord;
import org.neo4j.kernel.impl.store.record.Record;
import org.neo4j.kernel.impl.store.record.RecordLoad;

/**
 * A store for {@link #updateRecord(AbstractBaseRecord, PageCursorTracer) updating} and
 * {@link #getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}  getting} records.
 *
 * There are two ways of getting records, either one-by-one using
 * {@link #getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}, passing in record retrieved from {@link #newRecord()}.
 * This to make a conscious decision about who will create the record instance and in that process figure out
 * ways to reduce number of record instances created.
 * <p>
 * The other way is to use {@link #openPageCursorForReading(long, PageCursorTracer)} to open a cursor and use it to read records using
 * {@link #getRecordByCursor(long, AbstractBaseRecord, RecordLoad, PageCursor)}. A {@link PageCursor} can be ket open
 * to read multiple records before closing it.
 *
 * @param <RECORD> type of {@link AbstractBaseRecord}.
 */
public interface RecordStore<RECORD extends AbstractBaseRecord> extends IdSequence
{
    /**
     * @return the {@link Path} that backs this store.
     */
    Path getStorageFile();

    /**
     * @return high id of this store, i.e an id higher than any in use record.
     */
    long getHighId();

    /**
     * @param cursorTracer underlying page cursor tracer.
     * @return highest id in use in this store.
     */
    long getHighestPossibleIdInUse( PageCursorTracer cursorTracer );

    /**
     * Sets highest id in use for this store. This is for when records are applied to this store where
     * the ids have been generated through some other means. Having an up to date highest possible id
     * makes sure that closing this store truncates at the right place and that "all record scans" can
     * see all records.
     *
     * @param highestIdInUse highest id that is now in use in this store.
     */
    void setHighestPossibleIdInUse( long highestIdInUse );

    /**
     * @return a new record instance for receiving data by {@link #getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}.
     */
    RECORD newRecord();

    /**
     * Reads a record from the store into {@code target}. Depending on {@link RecordLoad} given there will
     * be different behavior, although the {@code target} record will be marked with the specified
     * {@code id} after participating in this method call.
     * <ul>
     * <li>{@link RecordLoad#CHECK}: As little data as possible is read to determine whether the record
     *     is in use or not. If not in use then no more data will be loaded into the target record and
     *     the data of the record will be {@link AbstractBaseRecord#clear() cleared}.</li>
     * <li>{@link RecordLoad#NORMAL}: Just like {@link RecordLoad#CHECK}, but with the difference that
     *     an {@link InvalidRecordException} will be thrown if the record isn't in use.</li>
     * <li>{@link RecordLoad#FORCE}: The entire contents of the record will be loaded into the target record
     *     regardless if the record is in use or not. This leaves no guarantees about the data in the record
     *     after this method call, except that the id will be the specified {@code id}.
     * <li>{@link RecordLoad#ALWAYS}: Similar to {@link RecordLoad#FORCE}, except the sanity checks on
     *     the record data is always enabled.</li>
     *
     * @param id the id of the record to load.
     * @param target record where data will be loaded into. This record will have its id set to the specified
     * {@code id} as part of this method call.
     * @param mode loading behaviour, read more in method description.
     * @param cursorTracer underlying page cursor tracer.
     * @return the record that was passed in, for convenience.
     * @throws InvalidRecordException if record not in use and the {@code mode} allows for throwing.
     */
    RECORD getRecord( long id, RECORD target, RecordLoad mode, PageCursorTracer cursorTracer ) throws InvalidRecordException;

    /**
     * Opens a {@link PageCursor} on this store, capable of reading records using
     * {@link #getRecordByCursor(long, AbstractBaseRecord, RecordLoad, PageCursor)}.
     * The caller is responsible for closing it when done with it.
     *
     * @param id cursor will initially be placed at the page containing this record id.
     * @param cursorTracer underlying page cursor tracer.
     * @return PageCursor for reading records.
     */
    PageCursor openPageCursorForReading( long id, PageCursorTracer cursorTracer );

    /**
     * Opens a {@link PageCursor} on this store, capable of reading records using
     * {@link #getRecordByCursor(long, AbstractBaseRecord, RecordLoad, PageCursor)}.
     * The caller is responsible for closing it when done with it.
     * The opened cursor will make use of pre-fetching for optimal scanning performance.
     *
     * @param id cursor will initially be placed at the page containing this record id.
     * @param cursorTracer underlying page cursor tracer.
     * @return PageCursor for reading records.
     */
    PageCursor openPageCursorForReadingWithPrefetching( long id, PageCursorTracer cursorTracer );

    /**
     * Opens a {@link PageCursor} on this store, capable of writing records using
     * {@link #updateRecord(AbstractBaseRecord, IdUpdateListener, PageCursor, PageCursorTracer)}.
     * The caller is responsible for closing it when done with it.
     *
     * @param id cursor will initially be placed at the page containing this record id.
     * @param cursorTracer underlying page cursor tracer.
     * @return PageCursor for writing records.
     */
    PageCursor openPageCursorForWriting( long id, PageCursorTracer cursorTracer );

    /**
     * Reads a record from the store into {@code target}, see
     * {@link RecordStore#getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}.
     * <p>
     * The provided page cursor will be used to get the record, and in doing this it will be redirected to the
     * correct page if needed.
     *
     * @param id the record id, understood to be the absolute reference to the store.
     * @param target the record to fill.
     * @param mode loading behaviour, read more in {@link RecordStore#getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}.
     * @param cursor the PageCursor to use for record loading.
     * @throws InvalidRecordException if record not in use and the {@code mode} allows for throwing.
     */
    void getRecordByCursor( long id, RECORD target, RecordLoad mode, PageCursor cursor ) throws InvalidRecordException;

    /**
     * Reads a record from the store into {@code target}, see
     * {@link RecordStore#getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}.
     * <p>
     * This method requires that the cursor page and offset point to the first byte of the record in target on calling.
     * The provided page cursor will be used to get the record, and in doing this it will be redirected to the
     * next page if the input record was the last on it's page.
     *
     * @param target the record to fill.
     * @param mode loading behaviour, read more in {@link RecordStore#getRecord(long, AbstractBaseRecord, RecordLoad, PageCursorTracer)}.
     * @param cursor the PageCursor to use for record loading.
     * @throws InvalidRecordException if record not in use and the {@code mode} allows for throwing.
     */
    void nextRecordByCursor( RECORD target, RecordLoad mode, PageCursor cursor ) throws InvalidRecordException;

    /**
     * For stores that have other stores coupled underneath, the "top level" record will have a flag
     * saying whether or not it's light. Light means that no records from the coupled store have been loaded yet.
     * This method can load those records and enrich the target record with those, marking it as heavy.
     *
     * @param record record to make heavy, if not already.
     * @param cursorTracer underlying page cursor tracer.
     */
    void ensureHeavy( RECORD record, PageCursorTracer cursorTracer );

    /**
     * Reads records that belong together, a chain of records that as a whole forms the entirety of a data item.
     *
     * @param firstId record id of the first record to start loading from.
     * @param mode {@link RecordLoad} mode.
     * @param guardForCycles Set to {@code true} if we need to take extra care in guarding for cycles in the chain.
     * When a cycle is found, a {@link RecordChainCycleDetectedException} will be thrown.
     * If {@code false}, then chain cycles will likely end up causing an {@link OutOfMemoryError}.
     * A cycle would only occur if the store is inconsistent, though.
     * @param cursorTracer underlying page cursor tracer
     * @return {@link Collection} of records in the loaded chain.
     * @throws InvalidRecordException if some record not in use and the {@code mode} is allows for throwing.
     */
    List<RECORD> getRecords( long firstId, RecordLoad mode, boolean guardForCycles, PageCursorTracer cursorTracer ) throws InvalidRecordException;

    /**
     * Streams records that belong together, a chain of records that as a whole forms the entirety of a data item.
     *
     * @param firstId record id of the first record to start loading from.
     * @param mode {@link RecordLoad} mode.
     * @param guardForCycles Set to {@code true} if we need to take extra care in guarding for cycles in the chain.
     * When a cycle is found, a {@link RecordChainCycleDetectedException} will be thrown.
     * If {@code false}, then chain cycles will likely end up causing an {@link OutOfMemoryError}.
     * A cycle would only occur if the store is inconsistent, though.
     * @param cursorTracer underlying page cursor tracer
     * @param subscriber The subscriber of the data, will receive records until the subscriber returns <code>false</code>
     */
    void streamRecords( long firstId, RecordLoad mode, boolean guardForCycles, PageCursorTracer cursorTracer,
                        RecordSubscriber<RECORD> subscriber );

    /**
     * Returns another record id which the given {@code record} references, if it exists in a chain of records.
     *
     * @param record to read the "next" reference from.
     * @return record id of "next" record that the given {@code record} references, or {@link Record#NULL_REFERENCE}
     * if the record doesn't reference a next record.
     */
    long getNextRecordReference( RECORD record );

    /**
     * Updates this store with the contents of {@code record} at the record id
     * {@link AbstractBaseRecord#getId() specified} by the record. The whole record will be written if
     * the given record is {@link AbstractBaseRecord#inUse() in use}, not necessarily so if it's not in use.
     *
     * @param record containing data to write to this store at the {@link AbstractBaseRecord#getId() id}
     * specified by the record.
     * @param cursorTracer underlying page cursor tracer.
     */
    default void updateRecord( RECORD record, IdUpdateListener idUpdates, PageCursorTracer cursorTracer )
    {
        try ( PageCursor cursor = openPageCursorForWriting( 0, cursorTracer ) )
        {
            updateRecord( record, idUpdates, cursor, cursorTracer );
        }
    }

    void updateRecord( RECORD record, IdUpdateListener idUpdates, PageCursor cursor, PageCursorTracer cursorTracer );

    default void updateRecord( RECORD record , PageCursorTracer cursorTracer )
    {
        updateRecord( record, IdUpdateListener.DIRECT, cursorTracer );
    }

    /**
     * @return number of bytes each record in this store occupies. All records in a store is of the same size.
     */
    int getRecordSize();

    /**
     * @deprecated since it's exposed through the generic {@link RecordStore} interface although only
     * applicable to one particular type of of implementation of it.
     * @return record "data" size, only applicable to dynamic record stores where record size may be specified
     * at creation time and later used every time the store is opened. Data size refers to number of bytes
     * of a record without header information, such as "inUse" and "next".
     */
    @Deprecated
    int getRecordDataSize();

    /**
     * @return underlying storage is assumed to work with pages. This method returns number of records that
     * will fit into each page.
     */
    int getRecordsPerPage();

    /**
     * Closes this store and releases any resource attached to it.
     */
    void close();

    /**
     * Flushes all pending {@link #updateRecord(AbstractBaseRecord, PageCursorTracer) updates} to underlying storage.
     * This call is blocking and will ensure all updates since last call to this method are durable
     * once the call returns.
     */
    void flush( PageCursorTracer cursorTracer );

    /**
     * Some stores may have meta data stored in the header of the store file. Since all records in a store
     * are of the same size the means of storing that meta data is to occupy one or more records at the
     * beginning of the store (0...).
     *
     * @return the number of records in the beginning of the file that are reserved for header meta data.
     */
    int getNumberOfReservedLowIds();

    /**
     * Returns store header (see {@link #getNumberOfReservedLowIds()}) as {@code int}. Exposed like this
     * for convenience since all known store headers are ints.
     *
     * @return store header as an int value, e.g the first 4 bytes of the first (reserved) record in this store.
     */
    int getStoreHeaderInt();

    /**
     * Called once all changes to a record is ready to be converted into a command.
     *
     * @param record record to prepare, potentially updating it with more information before converting into a command.
     * @param cursorTracer underlying page cursor tracer
     */
    void prepareForCommit( RECORD record, PageCursorTracer cursorTracer );

    /**
     * Called once all changes to a record is ready to be converted into a command.
     * WARNING this is for advanced use, please consider using {@link #prepareForCommit(AbstractBaseRecord, PageCursorTracer)} instead.
     *
     * @param record record to prepare, potentially updating it with more information before converting into a command.
     * @param idSequence {@link IdSequence} to use for potentially generating additional ids required by this record.
     * @param cursorTracer underlying page cursor tracer
     */
    void prepareForCommit( RECORD record, IdSequence idSequence, PageCursorTracer cursorTracer );

    /**
     * Scan the given range of records both inclusive, and pass all the in-use ones to the given processor, one by one.
     *
     * The record passed to the NodeRecordScanner is reused instead of reallocated for every record, so it must be
     * cloned if you want to save it for later.
     * @param visitor {@link Visitor} notified about all records.
     * @param cursorTracer underlying page cursor tracer.
     * @throws EXCEPTION on error reading from store.
     */
    <EXCEPTION extends Exception> void scanAllRecords( Visitor<RECORD,EXCEPTION> visitor, PageCursorTracer cursorTracer ) throws EXCEPTION;

    class Delegator<R extends AbstractBaseRecord> implements RecordStore<R>
    {
        private final RecordStore<R> actual;

        @Override
        public void setHighestPossibleIdInUse( long highestIdInUse )
        {
            actual.setHighestPossibleIdInUse( highestIdInUse );
        }

        @Override
        public R newRecord()
        {
            return actual.newRecord();
        }

        @Override
        public R getRecord( long id, R target, RecordLoad mode, PageCursorTracer cursorTracer ) throws InvalidRecordException
        {
            return actual.getRecord( id, target, mode, cursorTracer );
        }

        @Override
        public PageCursor openPageCursorForReading( long id, PageCursorTracer cursorTracer )
        {
            return actual.openPageCursorForReading( id, cursorTracer );
        }

        @Override
        public PageCursor openPageCursorForReadingWithPrefetching( long id, PageCursorTracer cursorTracer )
        {
            return actual.openPageCursorForReadingWithPrefetching( id, cursorTracer );
        }

        @Override
        public PageCursor openPageCursorForWriting( long id, PageCursorTracer cursorTracer )
        {
            return actual.openPageCursorForWriting( id, cursorTracer );
        }

        @Override
        public void getRecordByCursor( long id, R target, RecordLoad mode, PageCursor cursor ) throws InvalidRecordException
        {
            actual.getRecordByCursor( id, target, mode, cursor );
        }

        @Override
        public void nextRecordByCursor( R target, RecordLoad mode, PageCursor cursor ) throws InvalidRecordException
        {
            actual.nextRecordByCursor( target, mode, cursor );
        }

        @Override
        public List<R> getRecords( long firstId, RecordLoad mode, boolean guardForCycles, PageCursorTracer cursorTracer ) throws InvalidRecordException
        {
            return actual.getRecords( firstId, mode, guardForCycles, cursorTracer );
        }

        @Override
        public void streamRecords( long firstId, RecordLoad mode, boolean guardForCycles, PageCursorTracer cursorTracer, RecordSubscriber<R> subscriber )
        {
            actual.streamRecords( firstId, mode, guardForCycles, cursorTracer, subscriber );
        }

        @Override
        public long getNextRecordReference( R record )
        {
            return actual.getNextRecordReference( record );
        }

        public Delegator( RecordStore<R> actual )
        {
            this.actual = actual;
        }

        @Override
        public long nextId( PageCursorTracer cursorTracer )
        {
            return actual.nextId( cursorTracer );
        }

        @Override
        public Path getStorageFile()
        {
            return actual.getStorageFile();
        }

        @Override
        public long getHighId()
        {
            return actual.getHighId();
        }

        @Override
        public long getHighestPossibleIdInUse( PageCursorTracer cursorTracer )
        {
            return actual.getHighestPossibleIdInUse( cursorTracer );
        }

        @Override
        public void updateRecord( R record, IdUpdateListener idUpdateListener, PageCursor cursor, PageCursorTracer cursorTracer )
        {
            actual.updateRecord( record, idUpdateListener, cursor, cursorTracer );
        }

        @Override
        public int getRecordSize()
        {
            return actual.getRecordSize();
        }

        @Override
        public int getRecordDataSize()
        {
            return actual.getRecordDataSize();
        }

        @Override
        public int getRecordsPerPage()
        {
            return actual.getRecordsPerPage();
        }

        @Override
        public int getStoreHeaderInt()
        {
            return actual.getStoreHeaderInt();
        }

        @Override
        public void close()
        {
            actual.close();
        }

        @Override
        public int getNumberOfReservedLowIds()
        {
            return actual.getNumberOfReservedLowIds();
        }

        @Override
        public void flush( PageCursorTracer cursorTracer )
        {
            actual.flush( cursorTracer );
        }

        @Override
        public void ensureHeavy( R record, PageCursorTracer cursorTracer )
        {
            actual.ensureHeavy( record, cursorTracer );
        }

        @Override
        public void prepareForCommit( R record, PageCursorTracer cursorTracer )
        {
            actual.prepareForCommit( record, cursorTracer );
        }

        @Override
        public void prepareForCommit( R record, IdSequence idSequence, PageCursorTracer cursorTracer )
        {
            actual.prepareForCommit( record, idSequence, cursorTracer );
        }

        @Override
        public <EXCEPTION extends Exception> void scanAllRecords( Visitor<R,EXCEPTION> visitor, PageCursorTracer cursorTracer ) throws EXCEPTION
        {
            actual.scanAllRecords( visitor, cursorTracer );
        }
    }
}

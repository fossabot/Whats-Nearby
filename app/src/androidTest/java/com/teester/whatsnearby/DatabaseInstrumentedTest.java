package com.teester.whatsnearby;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.teester.whatsnearby.data.OsmObject;
import com.teester.whatsnearby.data.database.AppDatabase;
import com.teester.whatsnearby.data.database.VisitedLocation;
import com.teester.whatsnearby.data.database.VisitedLocationDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {

	@Rule
	public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

	private AppDatabase mDatabase;
	private VisitedLocationDao visitedLocationDao;
	private OsmObject osmObject = new OsmObject(1, "", "1", "", 1, 1, 1);
	private OsmObject osmObject2 = new OsmObject(2, "", "2", "", 1, 1, 1);
	private OsmObject osmObject3 = new OsmObject(3, "", "3", "", 1, 1, 1);
	private OsmObject osmObject4 = new OsmObject(4, "", "4", "", 1, 1, 1);
	private OsmObject osmObject5 = new OsmObject(5, "", "5", "", 1, 1, 1);

	@Before
	public void initDb() {
		mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
				AppDatabase.class)
				.allowMainThreadQueries()
				.build();

		visitedLocationDao = mDatabase.visitedLocationDao();
	}

	@After
	public void closeDb() {
		mDatabase.close();
	}

	@Test
	public void onFetchingLocationsShouldGetEmptyListIfTableIsEmpty() {
		List<VisitedLocation> LocationList = visitedLocationDao.getAllVisitedLocations();
		assertTrue(LocationList.isEmpty());
	}

	@Test
	public void onInsertingLocationsCheckIfRowCountIsCorrect() {
		visitedLocationDao.insert(new VisitedLocation(osmObject));
		visitedLocationDao.insert(new VisitedLocation(osmObject2));
		visitedLocationDao.insert(new VisitedLocation(osmObject3));
		visitedLocationDao.insert(new VisitedLocation(osmObject4));
		visitedLocationDao.insert(new VisitedLocation(osmObject5));

		assertEquals(5, visitedLocationDao.getAllVisitedLocations().size());
	}

	@Test
	public void onUpdatingALocationCheckIfUpdateHappensCorrectly() {
		VisitedLocation visitedLocation = new VisitedLocation(osmObject);
		visitedLocationDao.insert(visitedLocation);

		visitedLocation = visitedLocationDao.findByOsmId(osmObject.getId());
		visitedLocation.setName("New Name");
		visitedLocationDao.update(visitedLocation);

		assertEquals(1, visitedLocationDao.getAllVisitedLocations().size());
		assertEquals("New Name", visitedLocationDao.findByOsmId(osmObject.getId()).getName());
	}

	@Test
	public void onLocationDeletionCheckIfLocationIsDeletedFromTable() {
		visitedLocationDao.insert(new VisitedLocation(osmObject));
		visitedLocationDao.insert(new VisitedLocation(osmObject2));
		visitedLocationDao.insert(new VisitedLocation(osmObject3));
		visitedLocationDao.insert(new VisitedLocation(osmObject4));
		visitedLocationDao.insert(new VisitedLocation(osmObject5));

		visitedLocationDao.delete(visitedLocationDao.findByOsmId(osmObject2.getId()));
		assertNull(visitedLocationDao.findByOsmId(osmObject2.getId()));
	}

	@Test
	public void onFindByLocationCheckLocationFound() {
		visitedLocationDao.insert(new VisitedLocation(osmObject));
		visitedLocationDao.insert(new VisitedLocation(osmObject2));
		visitedLocationDao.insert(new VisitedLocation(osmObject3));
		visitedLocationDao.insert(new VisitedLocation(osmObject4));
		visitedLocationDao.insert(new VisitedLocation(osmObject5));

		List<VisitedLocation> list = visitedLocationDao.findByLocation(1, 1);
		assertEquals(5, list.size());
	}

	@Test
	public void onFindByLocationCheckLocationNotFound() {
		visitedLocationDao.insert(new VisitedLocation(osmObject));
		visitedLocationDao.insert(new VisitedLocation(osmObject2));
		visitedLocationDao.insert(new VisitedLocation(osmObject3));
		visitedLocationDao.insert(new VisitedLocation(osmObject4));
		visitedLocationDao.insert(new VisitedLocation(osmObject5));

		List<VisitedLocation> list = visitedLocationDao.findByLocation(10, 10);
		assertEquals(0, list.size());
	}
}

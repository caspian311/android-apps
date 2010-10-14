package net.todd.scorekeeper;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import android.content.Context;

public class PersistorTest {
	private Context context;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private File tempFile;
	private String inputFilename;
	private int outputFileMode;
	private String outputFilename;
	
	@BeforeClass
	public static void setUpLogger() {
		Logger.setTestMode(true);
	}
	
	@AfterClass
	public static void tearDownLogger() {
		Logger.setTestMode(false);
	}

	@Before
	public void setUp() throws Exception {
		tempFile = File.createTempFile(getClass().getName(), ".data");
		
		context = mock(Context.class);
		
		fileInputStream = new FileInputStream(tempFile);
		doReturn(fileInputStream).when(context).openFileInput(anyString());
		fileOutputStream = new FileOutputStream(tempFile);
		doReturn(fileOutputStream).when(context).openFileOutput(anyString(), anyInt());
	}
	
	@After
	public void tearDown() throws Exception {
		tempFile.delete();
	}
	
	@Test
	public void loadedListOfEntitiesIsEmptyInitially() {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		List<Person> personList = persistor.load();
		
		assertTrue("Person list should be empty", personList.isEmpty());
	}
	
	@Test
	public void savingEntityThenLoadingItBackYeildsSameObject() {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		Person person1 = new Person();
		person1.setName(UUID.randomUUID().toString());
		Person person2 = new Person();
		person2.setName(UUID.randomUUID().toString());
		List<Person> items = Arrays.asList(person1, person2);
		persistor.persist(items);
		
		Persistor<Person> anotherPersistor = Persistor.create(Person.class, context);
		List<Person> personList = anotherPersistor.load();
		
		assertEquals(2, personList.size());
		assertEquals(person1, personList.get(0));
		assertEquals(person2, personList.get(1));
	}
	
	@Test
	public void savingStuffSavesItToTheFile() {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		List<Person> items = Arrays.asList(new Person());
		persistor.persist(items);

		assertTrue(tempFile.exists());
		long originalSize = tempFile.length();
		
		List<Person> newItems = Arrays.asList(new Person(), new Person());
		persistor.persist(newItems);
		
		long newSize = tempFile.length();
		
		assertTrue(originalSize < newSize);
	}
	
	@Test
	public void savingStuffSavesItToCorrectFile() throws FileNotFoundException {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		persistor.persist(Arrays.asList(new Person()));

		ArgumentCaptor<String> outputFilenameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Integer> outputFileModeCaptor = ArgumentCaptor.forClass(Integer.class);
		verify(context).openFileOutput(outputFilenameCaptor.capture(), outputFileModeCaptor.capture());
		outputFilename = outputFilenameCaptor.getValue();
		outputFileMode = outputFileModeCaptor.getValue();
		
		assertEquals(Person.class.getName() + ".data", outputFilename);
		assertEquals(Context.MODE_PRIVATE, outputFileMode);
	}
	
	@Test
	public void loadingStuffLoadsFromTheCorrectFile() throws FileNotFoundException {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		persistor.persist(Arrays.asList(new Person()));
		persistor.load();

		ArgumentCaptor<String> inputFilenameCaptor = ArgumentCaptor.forClass(String.class);
		verify(context, atLeastOnce()).openFileInput(inputFilenameCaptor.capture());
		inputFilename = inputFilenameCaptor.getValue();
		
		assertEquals(Person.class.getName() + ".data", inputFilename);
	}
	
	@Test
	public void nextIdAlwaysIncrementsByOne() throws FileNotFoundException {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		int firstId = persistor.nextId();
		
		Persistor<Person> anotherPersistor = Persistor.create(Person.class, context);
		int secondId = anotherPersistor.nextId();
		
		Persistor<Person> yetAnotherPersistor = Persistor.create(Person.class, context);
		int thirdId = yetAnotherPersistor.nextId();

		assertEquals(firstId + 1, secondId);
		assertEquals(secondId + 1, thirdId);
	}
	
	@Test
	public void loadingIdsLoadsFromTheCorrectFile() throws FileNotFoundException {
		Persistor<Person> persistor = Persistor.create(Person.class, context);
		persistor.nextId();

		ArgumentCaptor<String> inputFilenameCaptor = ArgumentCaptor.forClass(String.class);
		verify(context, atLeastOnce()).openFileInput(inputFilenameCaptor.capture());
		inputFilename = inputFilenameCaptor.getValue();
		
		assertEquals(Person.class.getName() + ".id", inputFilename);
	}
	
	private static class Person implements Serializable {
		private static final long serialVersionUID = 3120568188872493283L;
		
		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Person other = (Person) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
}
package org.azkwf.munchkin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.azkwf.munchkin.entity.DatasourceEntity;

public class DatasourceManager {

	private static final DatasourceManager INSTANCE = new DatasourceManager();

	private Map<String, DatasourceEntity> datasources;

	private DatasourceManager() {
		datasources = new HashMap<String, DatasourceEntity>();
	}

	public static DatasourceManager getInstance() {
		return INSTANCE;
	}

	public void load(final File file) throws IOException {
		Properties p = new Properties();
		p.load(new FileInputStream(file));

		Pattern ptn = Pattern.compile("^([^\\.]+).*$");
		Set<String> keys = new HashSet<String>();
		for (Object o : p.keySet()) {
			String key = o.toString();

			Matcher m = ptn.matcher(key);
			if (m.find()) {
				keys.add(m.group(1));
			}
		}

		for (String id : keys) {
			DatasourceEntity datasource = new DatasourceEntity();
			datasource.setId(id);
			datasource.setName(p.getProperty(String.format("%s.name", id)));
			datasource.setUser(p.getProperty(String.format("%s.user", id)));
			datasource.setPassword(p.getProperty(String.format("%s.password", id)));
			datasource.setUrl(p.getProperty(String.format("%s.url", id)));
			datasource.setDriver(p.getProperty(String.format("%s.driver", id)));
			datasources.put(id, datasource);
		}

	}

	public List<DatasourceEntity> getDatasouces() {
		List<DatasourceEntity> list = new ArrayList<DatasourceEntity>();
		for (String key : datasources.keySet()) {
			list.add(datasources.get(key));
		}
		Collections.sort(list, new Comparator<DatasourceEntity>() {
			@Override
			public int compare(DatasourceEntity o1, DatasourceEntity o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return list;
	}

	public void store(final File file) throws IOException {
		Properties p = new Properties();

		for (String id : datasources.keySet()) {
			DatasourceEntity datasource = datasources.get(id);
			p.setProperty(String.format("%s.name", id), datasource.getName());
			p.setProperty(String.format("%s.user", id), datasource.getUser());
			p.setProperty(String.format("%s.password", id), datasource.getPassword());
			p.setProperty(String.format("%s.url", id), datasource.getUrl());
			p.setProperty(String.format("%s.driver", id), datasource.getDriver());
		}

		p.store(new FileOutputStream(file), "");
	}
}

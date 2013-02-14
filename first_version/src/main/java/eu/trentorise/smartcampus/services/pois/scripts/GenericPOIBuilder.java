package eu.trentorise.smartcampus.services.pois.scripts;

import it.sayservice.platform.core.message.Core.Address;
import it.sayservice.platform.core.message.Core.Coordinate;
import it.sayservice.platform.core.message.Core.POI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.protobuf.Message;

import eu.trentorise.smartcampus.services.pois.data.message.Pois.DBPOI;
import eu.trentorise.smartcampus.services.pois.data.message.Pois.DBTag;
import eu.trentorise.smartcampus.services.pois.data.message.Pois.GenericPOI;

public class GenericPOIBuilder {
	
	protected final Log log = LogFactory.getLog(getClass());

	public List<Message> buildGenericPOIs(List<DBPOI> pois, List<DBTag> tags) {
		Map<String, String> tagMaps = new TreeMap<String, String>();
		for (DBTag tag: tags) {
			tagMaps.put(tag.getPoidbPoiId(), tag.getElement());
		}
		
		List<Message> result = new ArrayList<Message>();
		for (DBPOI poi: pois) {
			try {
			GenericPOI.Builder builder = GenericPOI.newBuilder();
			builder.setId(poi.getPoiId());
			String id[] = poi.getPoiId().split("@");
			builder.setTitle(id[0]);
			builder.setSource("Smart Campus Lab");
			String tag = "";
			if (tagMaps.containsKey(poi.getPoiId())) {
				tag = tagMaps.get(poi.getPoiId());
			}
			builder.setType(tag);
			builder.setPoiData(buildPOI(poi, tag));
			result.add(builder.build());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Unable to convert from POI to GenericPOI");
			}
		}
		
		return result;
	}
	
	private POI buildPOI(DBPOI dbpoi, String tag) {
		POI.Builder poiBuilder = POI.newBuilder();
		
		Address.Builder addressBuilder = Address.newBuilder();
		addressBuilder.setCity(dbpoi.getCity());
		addressBuilder.setCountry(dbpoi.getCountry());
		addressBuilder.setPostalCode(dbpoi.getPostalCode());
		addressBuilder.setRegion(dbpoi.getRegion());
		addressBuilder.setState(dbpoi.getState());
		addressBuilder.setStreet(dbpoi.getStreet());
//		addressBuilder.setLang("");
		poiBuilder.setAddress(addressBuilder.build());
		
		Coordinate.Builder coordBuilder = Coordinate.newBuilder();
		coordBuilder.setLatitude(dbpoi.getLatitude());
		coordBuilder.setLongitude(dbpoi.getLongitude());
//	coordBuilder.setAltitude(0);		
//		coordBuilder.setSrsName("");
		poiBuilder.setCoordinate(coordBuilder.build());
		
		poiBuilder.setDatasetId(dbpoi.getDatasetId());
		poiBuilder.setPoiId(dbpoi.getPoiId());
		poiBuilder.addTags(tag);
		
		return poiBuilder.build();
	}
	
}

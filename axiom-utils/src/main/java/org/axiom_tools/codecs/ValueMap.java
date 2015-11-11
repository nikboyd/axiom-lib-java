package org.axiom_tools.codecs;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
 * Contains mapped name + value pairs. 
 * A convenience for consuming and producing JSON without a predefined schema or class.
 * Also, compares such structures and reports their differences.
 * @author Nik Boyd <nik_boyd@gensler.com>
 */
@XmlRootElement
public class ValueMap {

    private static final String Dot = ".";
    private static final String Colon = " :";
    private static final String Empty = "";
    private static final String Blank = " ";
    private static final String Quote = "\"";
    private static final String BraceL = "{";
    private static final String BraceR = "}";

    public static final String ID = "id";
    public static final String Messages = "messages";
    private static final String NamedValues = "namedValues";
    private static final String Prefix = BraceL + Blank + Quote + NamedValues + Quote + Blank + Colon;

    private static final String Quoted = "'";
    private static final String Difference = "%s: %s != %s";
    private static final String Index = "%s[%d]";
    private static final String Brackets = "[]";
    private static final String Length = Brackets + ".length";
    
    private boolean unwrapped = true;
    private final Map<String, Object> namedValues = new HashMap();
    
    /**
     * Returns a new ValueMape.
     * @param payload a JSON payload
     * @return a new ValueMap, or null
     */
    public static ValueMap fromJSON(String payload) {
        if (payload.isEmpty()) return null;
        if (!payload.contains(NamedValues)) {
            payload = Prefix + payload + BraceR;
        }
        return ModelCodec.to(ValueMap.class).fromJSON(payload);
    }
    
    /**
     * Returns a new ValueMap.
     * @param value an ID value
     * @return a new ValueMap
     */
    public static ValueMap withID(String value) {
        return initial().with(ID, value);
    }
    
    /**
     * Returns a new ValueMap.
     * @param value an ID value
     * @return a new ValueMap
     */
    public static ValueMap withID(Number value) {
        return initial().with(ID, value);
    }
    
    /**
     * Returns a new ValueMap.
     * @param messages some messages
     * @return a new ValueMap
     */
    public static ValueMap withMessages(String... messages) {
        return initial().withAll(Messages, messages);
    }
    
    /**
     * Returns a new (empty) ValueMap.
     * @return a new ValueMap
     */
    public static ValueMap initial() {
        return new ValueMap();
    }
    
    /**
     * Adds a named value to this map.
     * @param valueName a value name
     * @param namedValue the named value
     * @return this ValueMap
     */
    public ValueMap with(String valueName, String namedValue) {
        getNamedValues().put(valueName, namedValue);
        return this;
    }
    
    /**
     * Adds a named value to this map.
     * @param valueName a value name
     * @param namedValue the named value
     * @return this ValueMap
     */
    public ValueMap with(String valueName, Number namedValue) {
        getNamedValues().put(valueName, namedValue);
        return this;
    }
    
    /**
     * Adds a named value array to this map.
     * @param valueName a value name
     * @param namedValues the named values
     * @return this ValueMap
     */
    public ValueMap withAll(String valueName, String... namedValues) {
        getNamedValues().put(valueName, Arrays.asList(namedValues));
        return this;
    }
    
    /**
     * Adds a named value array to this map.
     * @param valueName a value name
     * @param namedValues the named values
     * @return this ValueMap
     */
    public ValueMap withAll(String valueName, Number... namedValues) {
        getNamedValues().put(valueName, Arrays.asList(namedValues));
        return this;
    }
    
    /**
     * Adds a named value map to this map.
     * @param valueName a value name
     * @param map a named value map
     * @return this ValueMap
     */
    public ValueMap with(String valueName, Map<String, Object> map) {
        getNamedValues().put(valueName, map);
        return this;
    }
    
    /**
     * Gets the ID.
     * @param <ValueType> a value type
     * @return a value for an ID
     */
    public <ValueType> ValueType getID() {
        return getValue(ID);
    }
    
    /**
     * Gets a value.
     * @param <ValueType> a value type
     * @param valueName a value name
     * @return a value
     */
    public <ValueType> ValueType getValue(String valueName) {
        String[] path = valueName.replace(Dot, Blank).split(Blank);
        Object result = getNamedValues();
        for (int index = 0; index < path.length; index++) {
            String name = path[index];
            String[] names = getParts(name);
            result = getValue(name, (Map<String, Object>)result);
            if (names.length > 1) {
                result = getValue(name, (List)result);
            }
        }
        return (ValueType)result;
    }
    
    private Object getValue(String valueName, List values) {
        return values.get(Integer.parseInt(getParts(valueName)[1]));
    }
    
    private Object getValue(String valueName, Map<String, Object> map) {
        return map.get(getParts(valueName)[0]);
    }
    
    private String[] getParts(String valueName) {
        String sample = valueName;
        for (char b : Brackets.toCharArray()) {
            sample = sample.replace(String.valueOf(b), Blank);
        }
        return sample.trim().split(Blank);
    }
    
    /**
     * The named values.
     * @return the named values
     */
    @XmlElement
    public Map<String, Object> getNamedValues() {
        return this.namedValues;
    }
    
    public void setNamedValues(Map<String, Object> values) {
        this.namedValues.putAll(values);
    }

    /**
     * Makes JSON unwrapped (if true).
     * @param unwrapJSON unwraps the JSON
     * @return this ValueMap
     */
    public ValueMap makeUnwrapped(boolean unwrapJSON) {
        this.unwrapped = unwrapJSON;
        return this;
    }
    
    /**
     * Returns the named values formatted as JSON.
     * @return a JSON payload
     */
    public String toJSON() {
        String result = ModelCodec.from(this).toJSON();
        if (!this.unwrapped) return result;

        int a = result.indexOf(NamedValues);
        a = result.indexOf(Colon, a + NamedValues.length());
        a = result.indexOf(BraceL, a + Colon.length());
        int b = result.length() - 1;
        return result.substring(a, b).trim();
    }
    
    /**
     * Returns the named values formatted as XML.
     * @return an XML payload
     */
    public String toXML() {
        return ModelCodec.from(this).toXML();
    }
    
    /**
     * Indicates whether this map resembles another (deeply).
     * @param map another value map
     * @return whether these maps resemble each other
     */
    public boolean resembles(ValueMap map) {
        if (map == null) return false;
        return reportDifferences(map).isEmpty();
    }
    
    /**
     * Reports any differences between this map and another.
     * @param map another value map
     * @return a list of differences (if any)
     */
    public List<String> reportDifferences(ValueMap map) {
        return reportEntryDifferences(Empty, getNamedValues(), map.getNamedValues());
    }
    
    /**
     * Detects and reports differences (deeply).
     * @param path a value path
     * @param mapA a map
     * @param mapB a comparable map
     * @return a list of differences, or empty
     */
    private List<String> reportEntryDifferences(String path, Map<String, Object> mapA, Map<String, Object> mapB) {
        List<String> results = new ArrayList();
        if (mapB == null) {
            results.add(reportMissing(path));
            return results;
        }

        for (String key : mapA.keySet()) {
            String pathKey = (path.isEmpty() ? Empty : path + Dot) + key;
            results.addAll(reportDifferences(pathKey, mapA.get(key), mapB.get(key)));
        }

        return results;
    }

    /**
     * Detects and reports differences between a pair of entries.
     * @param path a value path
     * @param entryA an entry
     * @param entryB a comparable entry
     * @return a list of differences, or empty
     */
    private List<String> reportDifferences(String path, Object entryA, Object entryB) {
        List<String> results = new ArrayList();
        if (entryA == null) {
            return results;
        }

        if (entryB == null) {
            results.add(reportMissing(path));
            return results;
        }

        if (entryA instanceof Map) {
            results.addAll(reportMapDifferences(path, entryA, entryB));
            return results;
        }

        if (entryA instanceof List) {
            results.addAll(reportListDifferences(path, entryA, entryB));
            return results;
        }
        else {
            results.addAll(reportValueDifference(path, entryA, entryB));
            return results;
        }
    }
    
    /**
     * Detects and reports differences between a pair of maps.
     * @param path a value path
     * @param mapA a map
     * @param mapB a comparable map
     * @return a list of differences, or empty
     */
    private List<String> reportMapDifferences(String path, Object mapA, Object mapB) {
        List<String> results = new ArrayList();
        if (mapB instanceof Map) {
            results.addAll(reportEntryDifferences(path, (Map)mapA, (Map)mapB));
        }
        else {
            results.add(reportTypeDifference(path, mapB));
        }
        return results;
    }
    
    /**
     * Detects and reports differences between a pair of lists.
     * @param path a value path
     * @param listA a list
     * @param listB a comparable list
     * @return a list of differences, or empty
     */
    private List<String> reportListDifferences(String path, Object listA, Object listB) {
        List<String> results = new ArrayList();
        if (listB instanceof List) {
            results.addAll(reportElementDifferences(path, (List)listA, (List)listB));
        }
        else {
            results.add(reportTypeDifference(path, listB));
        }
        return results;
    }

    /**
     * Detects and reports differences between a pair of lists.
     * @param path a value path
     * @param listA a list
     * @param listB a comparable list
     * @return a list of differences, or empty
     */
    private List<String> reportElementDifferences(String path, List<?> listA, List<?> listB) {
        List<String> results = new ArrayList();
        if (listA.size() != listB.size()) {
            results.add(String.format(Difference, path + Length, listA.size(), Empty + listB.size()));
            return results;
        }

        for (int index = 0; index < listA.size(); index++) {
            String listPath = String.format(Index, path, index);
            results.addAll(reportDifferences(listPath, listA.get(index), listB.get(index)));
        }
        return results;
    }
    
    /**
     * Detects and reports value differences.
     * @param path a value path
     * @param a a value
     * @param b a comparable value
     * @return a list of differences, or empty
     */
    private List<String> reportValueDifference(String path, Object a, Object b) {
        List<String> results = new ArrayList();
        if (a.equals(b)) {
            return results;
        }

        if (a instanceof String) {
            results.add(reportTextDifference(path, a.toString(), b.toString()));
        }
        else {
            results.add(String.format(Difference, path, a.toString(), b.toString()));
        }
        return results;
    }
    
    private String reportTextDifference(String path, String a, String b) {
        return String.format(Difference, path, Quoted + a + Quoted, Quoted + b + Quoted);
    }
    
    private String reportTypeDifference(String path, Object b) {
        return path + ": has different type != " + b.getClass().getSimpleName();
    }
    
    private String reportMissing(String path) {
        return path + " is missing";
    }
    
} // ValueMap

package com.dat3m.dartagnan.program.memory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.dat3m.dartagnan.program.memory.utils.IllegalMemoryAccessException;
import java.util.*;

public class Memory {

    private BiMap<Location, Address> map;
    private Map<String, Location> locationIndex;
    private Map<String, List<Address>> arrays;

    private int nextIndex = 0;

    public Memory(){
        map = HashBiMap.create();
        locationIndex = new HashMap<>();
        arrays = new HashMap<>();
    }

    public Location getLocationForAddress(Address address){
        return map.inverse().get(address);
    }

    public BoolExpr encode(com.dat3m.dartagnan.utils.EncodingConf conf){
    	Context ctx = conf.getCtx();
    	boolean bp = conf.getBP();
        BoolExpr enc = ctx.mkTrue();
        Set<Expr> expressions = new HashSet<>();

        for(List<Address> array : arrays.values()){
            int size = array.size();
            Expr e1 = array.get(0).toZ3Int(conf);
            if(!array.get(0).hasConstValue()) {
                expressions.add(e1);            	
            }

            for(int i = 1; i < size; i++){
                Expr e2 = array.get(i).toZ3Int(conf);
                Expr newAddress = bp ? ctx.mkBVAdd((BitVecExpr)e1, ctx.mkBV(1, 32)) : ctx.mkAdd((IntExpr)e1, ctx.mkInt(1));
				enc = ctx.mkAnd(enc, ctx.mkEq(newAddress, e2));
                if(!array.get(i).hasConstValue()) {
                    expressions.add(e2);                	
                }
                e1 = e2;
            }
        }
        for(Address address : map.values()){
        	if(address.hasConstValue()) {
        		Expr constantAddress = bp ? ctx.mkBV(address.getConstValue(), 32) : ctx.mkInt(address.getConstValue());
				enc = ctx.mkAnd(enc, ctx.mkEq(address.toZ3Int(conf), constantAddress));
        	}
        }
        return ctx.mkAnd(enc, ctx.mkDistinct(getAllAddresses().stream().map(a -> a.toZ3Int(conf)).toArray(Expr[]::new)));
    }

    public List<Address> malloc(String name, int size){
        if(!arrays.containsKey(name) && size > 0){
            List<Address> addresses = new ArrayList<>();
            for(int i = 0; i < size; i++){
                addresses.add(new Address(nextIndex++));
            }
            arrays.put(name, addresses);
            return addresses;
        }
        throw new IllegalMemoryAccessException("Illegal malloc for " + name);
    }

    public Location getOrCreateLocation(String name){
        if(!locationIndex.containsKey(name)){
            Location location = new Location(name, new Address(nextIndex++));
            map.put(location, location.getAddress());
            locationIndex.put(name, location);
            return location;
        }
        return locationIndex.get(name);
    }

    public ImmutableSet<Address> getAllAddresses(){
        Set<Address> result = new HashSet<>(map.values());
        for(List<Address> array : arrays.values()){
            result.addAll(array);
        }
        return ImmutableSet.copyOf(result);
    }
}
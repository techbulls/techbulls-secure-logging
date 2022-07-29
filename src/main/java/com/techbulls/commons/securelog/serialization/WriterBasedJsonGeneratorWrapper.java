package com.techbulls.commons.securelog.serialization;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class WriterBasedJsonGeneratorWrapper extends JsonGenerator {

    private JsonGenerator js;
    public WriterBasedJsonGeneratorWrapper(JsonGenerator js){
        this.js=js;
    }
    @Override
    public JsonGenerator setCodec(ObjectCodec objectCodec) {
        js.setCodec(objectCodec);
        return js;
    }

    @Override
    public ObjectCodec getCodec() {
        return js.getCodec();
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public JsonGenerator enable(Feature feature) {
        return null;
    }

    @Override
    public JsonGenerator disable(Feature feature) {
        return null;
    }

    @Override
    public boolean isEnabled(Feature feature) {
        return false;
    }

    @Override
    public int getFeatureMask() {
        return 0;
    }

    @Override
    public JsonGenerator setFeatureMask(int i) {
        return null;
    }

    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return null;
    }

    @Override
    public void writeStartArray() throws IOException {

    }

    @Override
    public void writeEndArray() throws IOException {

    }

    @Override
    public void writeStartObject() throws IOException {

    }

    @Override
    public void writeEndObject() throws IOException {

    }

    @Override
    public void writeFieldName(String s) throws IOException {

    }

    @Override
    public void writeFieldName(SerializableString serializableString) throws IOException {

    }

    @Override
    public void writeString(String s) throws IOException {

    }

    @Override
    public void writeString(char[] chars, int i, int i1) throws IOException {

    }

    @Override
    public void writeString(SerializableString serializableString) throws IOException {

    }

    @Override
    public void writeRawUTF8String(byte[] bytes, int i, int i1) throws IOException {

    }

    @Override
    public void writeUTF8String(byte[] bytes, int i, int i1) throws IOException {

    }

    @Override
    public void writeRaw(String s) throws IOException {

    }

    @Override
    public void writeRaw(String s, int i, int i1) throws IOException {

    }

    @Override
    public void writeRaw(char[] chars, int i, int i1) throws IOException {

    }

    @Override
    public void writeRaw(char c) throws IOException {

    }

    @Override
    public void writeRawValue(String s) throws IOException {

    }

    @Override
    public void writeRawValue(String s, int i, int i1) throws IOException {

    }

    @Override
    public void writeRawValue(char[] chars, int i, int i1) throws IOException {

    }

    @Override
    public void writeBinary(Base64Variant base64Variant, byte[] bytes, int i, int i1) throws IOException {

    }

    @Override
    public int writeBinary(Base64Variant base64Variant, InputStream inputStream, int i) throws IOException {
        return 0;
    }

    @Override
    public void writeNumber(int i) throws IOException {

    }

    @Override
    public void writeNumber(long l) throws IOException {

    }

    @Override
    public void writeNumber(BigInteger bigInteger) throws IOException {

    }

    @Override
    public void writeNumber(double v) throws IOException {

    }

    @Override
    public void writeNumber(float v) throws IOException {

    }

    @Override
    public void writeNumber(BigDecimal bigDecimal) throws IOException {

    }

    @Override
    public void writeNumber(String s) throws IOException {

    }

    @Override
    public void writeBoolean(boolean b) throws IOException {

    }

    @Override
    public void writeNull() throws IOException {

    }

    @Override
    public void writeObject(Object o) throws IOException {

    }

    @Override
    public void writeTree(TreeNode treeNode) throws IOException {

    }

    @Override
    public JsonStreamContext getOutputContext() {
        return null;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}

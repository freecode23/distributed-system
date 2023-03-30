/**
 * Autogenerated by Thrift Compiler (0.18.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class PrepareResponse implements org.apache.thrift.TBase<PrepareResponse, PrepareResponse._Fields>, java.io.Serializable, Cloneable, Comparable<PrepareResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PrepareResponse");

  private static final org.apache.thrift.protocol.TField HIGHEST_PROPOSAL_NUMBER_FIELD_DESC = new org.apache.thrift.protocol.TField("highestProposalNumber", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField ACCEPTED_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("acceptedValue", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new PrepareResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new PrepareResponseTupleSchemeFactory();

  public int highestProposalNumber; // required
  public int acceptedValue; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    HIGHEST_PROPOSAL_NUMBER((short)1, "highestProposalNumber"),
    ACCEPTED_VALUE((short)2, "acceptedValue");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // HIGHEST_PROPOSAL_NUMBER
          return HIGHEST_PROPOSAL_NUMBER;
        case 2: // ACCEPTED_VALUE
          return ACCEPTED_VALUE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    @Override
    public short getThriftFieldId() {
      return _thriftId;
    }

    @Override
    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __HIGHESTPROPOSALNUMBER_ISSET_ID = 0;
  private static final int __ACCEPTEDVALUE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.HIGHEST_PROPOSAL_NUMBER, new org.apache.thrift.meta_data.FieldMetaData("highestProposalNumber", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ACCEPTED_VALUE, new org.apache.thrift.meta_data.FieldMetaData("acceptedValue", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PrepareResponse.class, metaDataMap);
  }

  public PrepareResponse() {
  }

  public PrepareResponse(
    int highestProposalNumber,
    int acceptedValue)
  {
    this();
    this.highestProposalNumber = highestProposalNumber;
    setHighestProposalNumberIsSet(true);
    this.acceptedValue = acceptedValue;
    setAcceptedValueIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PrepareResponse(PrepareResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    this.highestProposalNumber = other.highestProposalNumber;
    this.acceptedValue = other.acceptedValue;
  }

  @Override
  public PrepareResponse deepCopy() {
    return new PrepareResponse(this);
  }

  @Override
  public void clear() {
    setHighestProposalNumberIsSet(false);
    this.highestProposalNumber = 0;
    setAcceptedValueIsSet(false);
    this.acceptedValue = 0;
  }

  public int getHighestProposalNumber() {
    return this.highestProposalNumber;
  }

  public PrepareResponse setHighestProposalNumber(int highestProposalNumber) {
    this.highestProposalNumber = highestProposalNumber;
    setHighestProposalNumberIsSet(true);
    return this;
  }

  public void unsetHighestProposalNumber() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __HIGHESTPROPOSALNUMBER_ISSET_ID);
  }

  /** Returns true if field highestProposalNumber is set (has been assigned a value) and false otherwise */
  public boolean isSetHighestProposalNumber() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __HIGHESTPROPOSALNUMBER_ISSET_ID);
  }

  public void setHighestProposalNumberIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __HIGHESTPROPOSALNUMBER_ISSET_ID, value);
  }

  public int getAcceptedValue() {
    return this.acceptedValue;
  }

  public PrepareResponse setAcceptedValue(int acceptedValue) {
    this.acceptedValue = acceptedValue;
    setAcceptedValueIsSet(true);
    return this;
  }

  public void unsetAcceptedValue() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ACCEPTEDVALUE_ISSET_ID);
  }

  /** Returns true if field acceptedValue is set (has been assigned a value) and false otherwise */
  public boolean isSetAcceptedValue() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ACCEPTEDVALUE_ISSET_ID);
  }

  public void setAcceptedValueIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ACCEPTEDVALUE_ISSET_ID, value);
  }

  @Override
  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case HIGHEST_PROPOSAL_NUMBER:
      if (value == null) {
        unsetHighestProposalNumber();
      } else {
        setHighestProposalNumber((java.lang.Integer)value);
      }
      break;

    case ACCEPTED_VALUE:
      if (value == null) {
        unsetAcceptedValue();
      } else {
        setAcceptedValue((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case HIGHEST_PROPOSAL_NUMBER:
      return getHighestProposalNumber();

    case ACCEPTED_VALUE:
      return getAcceptedValue();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  @Override
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case HIGHEST_PROPOSAL_NUMBER:
      return isSetHighestProposalNumber();
    case ACCEPTED_VALUE:
      return isSetAcceptedValue();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof PrepareResponse)
      return this.equals((PrepareResponse)that);
    return false;
  }

  public boolean equals(PrepareResponse that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_highestProposalNumber = true;
    boolean that_present_highestProposalNumber = true;
    if (this_present_highestProposalNumber || that_present_highestProposalNumber) {
      if (!(this_present_highestProposalNumber && that_present_highestProposalNumber))
        return false;
      if (this.highestProposalNumber != that.highestProposalNumber)
        return false;
    }

    boolean this_present_acceptedValue = true;
    boolean that_present_acceptedValue = true;
    if (this_present_acceptedValue || that_present_acceptedValue) {
      if (!(this_present_acceptedValue && that_present_acceptedValue))
        return false;
      if (this.acceptedValue != that.acceptedValue)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + highestProposalNumber;

    hashCode = hashCode * 8191 + acceptedValue;

    return hashCode;
  }

  @Override
  public int compareTo(PrepareResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetHighestProposalNumber(), other.isSetHighestProposalNumber());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHighestProposalNumber()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.highestProposalNumber, other.highestProposalNumber);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetAcceptedValue(), other.isSetAcceptedValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAcceptedValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.acceptedValue, other.acceptedValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  @Override
  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  @Override
  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("PrepareResponse(");
    boolean first = true;

    sb.append("highestProposalNumber:");
    sb.append(this.highestProposalNumber);
    first = false;
    if (!first) sb.append(", ");
    sb.append("acceptedValue:");
    sb.append(this.acceptedValue);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PrepareResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public PrepareResponseStandardScheme getScheme() {
      return new PrepareResponseStandardScheme();
    }
  }

  private static class PrepareResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<PrepareResponse> {

    @Override
    public void read(org.apache.thrift.protocol.TProtocol iprot, PrepareResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // HIGHEST_PROPOSAL_NUMBER
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.highestProposalNumber = iprot.readI32();
              struct.setHighestProposalNumberIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ACCEPTED_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.acceptedValue = iprot.readI32();
              struct.setAcceptedValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    @Override
    public void write(org.apache.thrift.protocol.TProtocol oprot, PrepareResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(HIGHEST_PROPOSAL_NUMBER_FIELD_DESC);
      oprot.writeI32(struct.highestProposalNumber);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(ACCEPTED_VALUE_FIELD_DESC);
      oprot.writeI32(struct.acceptedValue);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PrepareResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public PrepareResponseTupleScheme getScheme() {
      return new PrepareResponseTupleScheme();
    }
  }

  private static class PrepareResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<PrepareResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PrepareResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetHighestProposalNumber()) {
        optionals.set(0);
      }
      if (struct.isSetAcceptedValue()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetHighestProposalNumber()) {
        oprot.writeI32(struct.highestProposalNumber);
      }
      if (struct.isSetAcceptedValue()) {
        oprot.writeI32(struct.acceptedValue);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PrepareResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.highestProposalNumber = iprot.readI32();
        struct.setHighestProposalNumberIsSet(true);
      }
      if (incoming.get(1)) {
        struct.acceptedValue = iprot.readI32();
        struct.setAcceptedValueIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}


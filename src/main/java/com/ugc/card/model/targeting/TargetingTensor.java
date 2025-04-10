package com.ugc.card.model.targeting;

import java.util.*;
import java.io.Serializable;
import lombok.Data;

@Data
public class TargetingTensor implements Serializable {
    private static final long serialVersionUID = 1L;
    private float[] data;
    private int[] shape;
    private int vectorSize;

    public TargetingTensor(float[] data, int[] shape) {
        this.shape = shape;
        this.data = data;
        this.vectorSize = shape[shape.length - 1];
    }

    public TargetingTensor(int[] shape) {
        this.shape = shape;
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        this.data = new float[size];
        this.vectorSize = shape[shape.length - 1];
    }

    public float getValue(int[] indices) {
        return data[calculateIndex(indices)];
    }

    public void setValue(int[] indices, float value) {
        data[calculateIndex(indices)] = value;
    }

    private int calculateIndex(int[] indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException("Indices length must match shape length");
        }
        int index = 0;
        int stride = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new IllegalArgumentException("Index out of bounds");
            }
            index += indices[i] * stride;
            stride *= shape[i];
        }
        return index;
    }

    public TargetingTensor add(TargetingTensor other) {
        if (!Arrays.equals(this.shape, other.shape)) {
            throw new IllegalArgumentException("Tensor shapes must match for addition");
        }
        TargetingTensor result = new TargetingTensor(this.shape);
        for (int i = 0; i < data.length; i++) {
            result.data[i] = this.data[i] + other.data[i];
        }
        return result;
    }

    public TargetingTensor multiply(float scalar) {
        TargetingTensor result = new TargetingTensor(this.shape);
        for (int i = 0; i < data.length; i++) {
            result.data[i] = this.data[i] * scalar;
        }
        return result;
    }

    public TargetingTensor reshape(int[] newShape) {
        int newSize = 1;
        for (int dim : newShape) {
            newSize *= dim;
        }
        if (newSize != data.length) {
            throw new IllegalArgumentException("New shape must have same number of elements");
        }
        TargetingTensor result = new TargetingTensor(newShape);
        System.arraycopy(this.data, 0, result.data, 0, data.length);
        return result;
    }

    public float[] flatten() {
        return Arrays.copyOf(data, data.length);
    }

    public static TargetingTensor zeros(int[] shape) {
        return new TargetingTensor(shape);
    }

    public static TargetingTensor ones(int[] shape) {
        TargetingTensor tensor = new TargetingTensor(shape);
        Arrays.fill(tensor.data, 1.0f);
        return tensor;
    }

    public static TargetingTensor fromSet(Set<Float> values, int[] shape) {
        TargetingTensor tensor = new TargetingTensor(shape);
        int i = 0;
        for (Float value : values) {
            tensor.data[i++] = value;
        }
        return tensor;
    }

    public int getVectorSize() {
        return vectorSize;
    }

    public float[] getValues() {
        return data;
    }

    public void setValues(float[] values) {
        if (values.length != data.length) {
            throw new IllegalArgumentException("Values length must match tensor size");
        }
        System.arraycopy(values, 0, data, 0, values.length);
    }

    public Set<TargetingChain> createTrainingData(Set<TargetingChain> chains) {
        // Convert chains to tensor format
        Set<TargetingChain> result = new HashSet<>();
        for (TargetingChain chain : chains) {
            // Create a tensor representation of the chain
            float[] vector = new float[vectorSize];
            // Fill vector based on chain components
            // This is a simplified version - you'll need to implement the actual conversion logic
            result.add(chain);
        }
        return result;
    }

    public int[] getShape() {
        return shape;
    }

    public float[] getData() {
        return data;
    }
} 
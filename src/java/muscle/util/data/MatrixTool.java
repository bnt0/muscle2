/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package muscle.util.data;

import java.io.Serializable;

/**
 *
 * @author Joris Borgdorff
 */
public class MatrixTool {
	public static int deepSizeOf(Serializable value, SerializableDatatype type) {
		int size = type.getDimensions()*4;

		switch (type.typeOf()) {
			case BOOLEAN_ARR:
				size += 4 + lengthOfMatrix(value, type);
				break;
			case BYTE_ARR:
				size += 4 + lengthOfMatrix(value, type);
				break;
			case SHORT_ARR:
				size += 4 + lengthOfMatrix(value, type)*2;
				break;
			case INT_ARR: case FLOAT_ARR:
				size += 4 + lengthOfMatrix(value, type)*4;
				break;
			case LONG_ARR: case DOUBLE_ARR:
				size += 4 + lengthOfMatrix(value, type)*8;
				break;
		}
		return size;
	}
	
	public static int lengthOfMatrix(Serializable value, SerializableDatatype type) {
		int[] dims = {1, 1, 1, 1};
		dimensionsOfMatrix(value, type, dims, 0);
		return dims[0]*dims[1]*dims[2]*dims[3];
	}
	
	public static int lengthOfArray(Serializable value, SerializableDatatype type) {
		switch (type.typeOf()) {
			case BOOLEAN_ARR:
				return ((boolean[])value).length;
			case BYTE_ARR:
				return ((byte[])value).length;
			case SHORT_ARR:
				return ((short[])value).length;
			case INT_ARR:
				return ((int[])value).length;
			case LONG_ARR:
				return ((long[])value).length;
			case FLOAT_ARR:
				return ((float[])value).length;
			case DOUBLE_ARR:
				return ((double[])value).length;
			case STRING_ARR:
				return ((String[])value).length;
			default:
				throw new IllegalArgumentException("Can only compute the length of arrays");
		}
	}

	static void dimensionsOfMatrix(Serializable value, SerializableDatatype type, int[] dims, int depth) {
		if (value instanceof Serializable[] && type != SerializableDatatype.STRING_ARR) {
			dims[depth] = ((Serializable[])value).length;
			if (dims[depth] > 0) {
				dimensionsOfMatrix(((Serializable[])value)[0], type, dims, depth + 1);
			}
		}
		else {
			dims[depth] = lengthOfArray(value, type);
		}
	}
	
	/**
	 * Gets the current data. If the datatype is a matrix, it converts it to an array first.
	 * @throws ClassCastException if the data is not a matrix of the correct type.
	 */
	static Serializable matrixToArray(Serializable value, SerializableDatatype type) {
		Serializable newValue = value;
		
		if (type.isMatrix()) {
			int dimX, dimY, dimZ, dimZZ, count = 0;
			switch (type) {
				case BOOLEAN_MATRIX_2D: {
					boolean[][] oldValue = (boolean[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new boolean[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case BYTE_MATRIX_2D: {
					byte[][] oldValue = (byte[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new byte[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case SHORT_MATRIX_2D: {
					short[][] oldValue = (short[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new short[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case INT_MATRIX_2D: {
					int[][] oldValue = (int[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new int[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case LONG_MATRIX_2D: {
					long[][] oldValue = (long[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new long[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case FLOAT_MATRIX_2D: {
					float[][] oldValue = (float[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new float[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case DOUBLE_MATRIX_2D: {
					double[][] oldValue = (double[][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
					newValue = new double[dimX*dimY];
					for (int i = 0; i < dimX; i++) {
						System.arraycopy(oldValue[i], 0, newValue, i*dimY, dimY);
					}
					} break;
				case BOOLEAN_MATRIX_3D: {
					boolean[][][] oldValue = (boolean[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new boolean[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case BYTE_MATRIX_3D: {
					byte[][][] oldValue = (byte[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new byte[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case SHORT_MATRIX_3D: {
					short[][][] oldValue = (short[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new short[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case INT_MATRIX_3D: {
					int[][][] oldValue = (int[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new int[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case LONG_MATRIX_3D: {
					long[][][] oldValue = (long[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new long[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case FLOAT_MATRIX_3D: {
					float[][][] oldValue = (float[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new float[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case DOUBLE_MATRIX_3D: {
					double[][][] oldValue = (double[][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
					newValue = new double[dimX*dimY*dimZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							System.arraycopy(oldValue[i][j], 0, newValue, count, dimZ);
							count += dimZ;
						}
					}
					} break;
				case BOOLEAN_MATRIX_4D: {
					boolean[][][][] oldValue = (boolean[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new boolean[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
				case BYTE_MATRIX_4D: {
					byte[][][][] oldValue = (byte[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new byte[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
				case SHORT_MATRIX_4D: {
					short[][][][] oldValue = (short[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new short[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
				case INT_MATRIX_4D: {
					int[][][][] oldValue = (int[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new int[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
				case LONG_MATRIX_4D: {
					long[][][][] oldValue = (long[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new long[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
				case FLOAT_MATRIX_4D: {
					float[][][][] oldValue = (float[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new float[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
				case DOUBLE_MATRIX_4D: {
					double[][][][] oldValue = (double[][][][])value;
					dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
					newValue = new double[dimX*dimY*dimZ*dimZZ];
					for (int i = 0; i < dimX; i++) {
						for (int j = 0; j < dimY; j++) {
							for (int k = 0; k < dimZ; k++) {
								System.arraycopy(oldValue[i][j][k], 0, newValue, count, dimZZ);
								count += dimZZ;
							}
						}
					}
					} break;
			}
		}
		
		return newValue;
	}
	
		/**
	 * If the given type is a matrix, it converts given array to a matrix
	 * @throws ClassCastException if the given data is not an array of the correct type.
	 */
	static Serializable arrayToMatrix(Serializable value, SerializableDatatype type, int dimX, int dimY, int dimZ, int dimZZ) {
		int count = 0;
		Serializable matrixValue = value;
		switch (type) {
			case BOOLEAN_MATRIX_2D: {
				boolean[][] newValue = new boolean[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case BYTE_MATRIX_2D: {
				byte[][] newValue = new byte[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case SHORT_MATRIX_2D: {
				short[][] newValue = new short[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case INT_MATRIX_2D: {
				int[][] newValue = new int[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case LONG_MATRIX_2D: {
				long[][] newValue = new long[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case FLOAT_MATRIX_2D: {
				float[][] newValue = new float[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case DOUBLE_MATRIX_2D: {
				double[][] newValue = new double[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(value, i*dimY, newValue[i], 0, dimY);
				}
				matrixValue = newValue; }
				break;
			case BOOLEAN_MATRIX_3D: {
				boolean[][][] newValue = new boolean[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case BYTE_MATRIX_3D: {
				byte[][][] newValue = new byte[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case SHORT_MATRIX_3D: {
				short[][][] newValue = new short[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case INT_MATRIX_3D: {
				int[][][] newValue = new int[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case LONG_MATRIX_3D: {
				long[][][] newValue = new long[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case FLOAT_MATRIX_3D: {
				float[][][] newValue = new float[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case DOUBLE_MATRIX_3D: {
				double[][][] newValue = new double[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(value, count, newValue[i][j], 0, dimZ);
						count += dimZ;
					}
				}
				matrixValue = newValue; }
				break;
			case BOOLEAN_MATRIX_4D: {
				boolean[][][][] newValue = new boolean[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
			case BYTE_MATRIX_4D: {
				byte[][][][] newValue = new byte[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
			case SHORT_MATRIX_4D: {
				short[][][][] newValue = new short[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
			case INT_MATRIX_4D: {
				int[][][][] newValue = new int[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
			case LONG_MATRIX_4D: {
				long[][][][] newValue = new long[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
			case FLOAT_MATRIX_4D: {
				float[][][][] newValue = new float[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
			case DOUBLE_MATRIX_4D: {
				double[][][][] newValue = new double[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(value, count, newValue[i][j][k], 0, dimZZ);
							count += dimZZ;
						}
					}
				}
				matrixValue = newValue; }
				break;
		}
		return matrixValue;
	}
	
	static Serializable deepCopy(Serializable value, SerializableDatatype type) {
		Serializable copyValue;
		int dimX, dimY, dimZ, dimZZ;
		switch (type) {	
			case BOOLEAN_ARR: {
				int len = ((boolean[])value).length;
				copyValue = new boolean[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case BYTE_ARR: {
				int len = ((byte[])value).length;
				copyValue = new byte[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case SHORT_ARR: {
				int len = ((short[])value).length;
				copyValue = new short[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case INT_ARR: {
				int len = ((int[])value).length;
				copyValue = new int[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case LONG_ARR: {
				int len = ((long[])value).length;
				copyValue = new long[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case FLOAT_ARR: {
				int len = ((float[])value).length;
				copyValue = new float[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case DOUBLE_ARR: {
				int len = ((double[])value).length;
				copyValue = new double[len];
				System.arraycopy(value, 0, copyValue, 0, len);
			} break;
			case BOOLEAN_MATRIX_2D: {
				boolean[][] oldValue = (boolean[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				boolean[][] newValue = new boolean[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case BYTE_MATRIX_2D: {
				byte[][] oldValue = (byte[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				byte[][] newValue = new byte[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case SHORT_MATRIX_2D: {
				short[][] oldValue = (short[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				short[][] newValue = new short[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case INT_MATRIX_2D: {
				int[][] oldValue = (int[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				int[][] newValue = new int[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case LONG_MATRIX_2D: {
				long[][] oldValue = (long[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				long[][] newValue = new long[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case FLOAT_MATRIX_2D: {
				float[][] oldValue = (float[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				float[][] newValue = new float[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case DOUBLE_MATRIX_2D: {
				double[][] oldValue = (double[][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0;
				double[][] newValue = new double[dimX][dimY];
				for (int i = 0; i < dimX; i++) {
					System.arraycopy(oldValue[i], 0, newValue[i], 0, dimY);
				}
				copyValue = newValue;
				} break;
			case BOOLEAN_MATRIX_3D: {
				boolean[][][] oldValue = (boolean[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				boolean[][][] newValue = new boolean[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case BYTE_MATRIX_3D: {
				byte[][][] oldValue = (byte[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				byte[][][] newValue = new byte[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case SHORT_MATRIX_3D: {
				short[][][] oldValue = (short[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				short[][][] newValue = new short[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case INT_MATRIX_3D: {
				int[][][] oldValue = (int[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				int[][][] newValue = new int[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case LONG_MATRIX_3D: {
				long[][][] oldValue = (long[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				long[][][] newValue = new long[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case FLOAT_MATRIX_3D: {
				float[][][] oldValue = (float[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				float[][][] newValue = new float[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case DOUBLE_MATRIX_3D: {
				double[][][] oldValue = (double[][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0;
				double[][][] newValue = new double[dimX][dimY][dimZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						System.arraycopy(oldValue[i][j], 0, newValue[i][j], 0, dimZ);
					}
				}
				copyValue = newValue;
				} break;
			case BOOLEAN_MATRIX_4D: {
				boolean[][][][] oldValue = (boolean[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				boolean[][][][] newValue = new boolean[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			case BYTE_MATRIX_4D: {
				byte[][][][] oldValue = (byte[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				byte[][][][] newValue = new byte[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			case SHORT_MATRIX_4D: {
				short[][][][] oldValue = (short[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				short[][][][] newValue = new short[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			case INT_MATRIX_4D: {
				int[][][][] oldValue = (int[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				int[][][][] newValue = new int[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			case LONG_MATRIX_4D: {
				long[][][][] oldValue = (long[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				long[][][][] newValue = new long[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			case FLOAT_MATRIX_4D: {
				float[][][][] oldValue = (float[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				float[][][][] newValue = new float[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			case DOUBLE_MATRIX_4D: {
				double[][][][] oldValue = (double[][][][])value;
				dimX = oldValue.length; dimY = dimX > 0 ? oldValue[0].length : 0; dimZ = dimY > 0 ? oldValue[0][0].length : 0; dimZZ = dimZ > 0 ? oldValue[0][0][0].length : 0;
				double[][][][] newValue = new double[dimX][dimY][dimZ][dimZZ];
				for (int i = 0; i < dimX; i++) {
					for (int j = 0; j < dimY; j++) {
						for (int k = 0; k < dimZ; k++) {
							System.arraycopy(oldValue[i][j][k], 0, newValue[i][j][k], 0, dimZZ);
						}
					}
				}
				copyValue = newValue;
				} break;
			default:
				throw new IllegalArgumentException("Serializable type " + type + " not recognized");
		}
		return copyValue;
	}
	
	public static Serializable initializeArray(SerializableDatatype type, int len) {
		switch (type.typeOf()) {
			case STRING_ARR:
				return new String[len];
			case BYTE_ARR:
				return new byte[len];
			case BOOLEAN_ARR:
				return new boolean[len];
			case SHORT_ARR:
				return new short[len];
			case INT_ARR:
				return new int[len];
			case LONG_ARR:
				return new long[len];
			case FLOAT_ARR:
				return new float[len];
			case DOUBLE_ARR:
				return new double[len];
			default:
				throw new IllegalArgumentException("Can only instantiate arrays, not type "+ type);
		}
	}
}
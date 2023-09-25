/*
 * Copyright (C) 2016 Jason Taylor.
 * Released as open-source under the Apache License, Version 2.0.
 * 
 * ============================================================================
 * | Joise
 * ============================================================================
 * 
 * Copyright (C) 2016 Jason Taylor
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ============================================================================
 * | Accidental Noise Library
 * | --------------------------------------------------------------------------
 * | Joise is a derivative work based on Josua Tippetts' C++ library:
 * | http://accidentalnoise.sourceforge.net/index.html
 * ============================================================================
 * 
 * Copyright (C) 2011 Joshua Tippetts
 * 
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 * 
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 * 
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 */
package net.ludocrypt.frostyheights.world.noise.module;

import java.util.Random;

import com.sudoplay.joise.ModuleInstanceMap;
import com.sudoplay.joise.ModuleMap;
import com.sudoplay.joise.ModulePropertyMap;
import com.sudoplay.joise.module.SeededModule;
import com.sudoplay.joise.noise.Noise;

public class VoronoiGen extends SeededModule {

	class CellularCache {

		double[] f = new double[4];
		double[] d = new double[4];
		double x, y, z, w, u, v;
		boolean valid = false;

	}

	public static enum DistanceType {
		DISTANCE,
		DISTANCE_SQUARED,
		MANHATTAN,
		MINKOWSKI,
		CHEBYSHEV
	}

	private double[] cache2D = new double[512];
	private double[] cache3D = new double[1024];
	private double[] cache4D = new double[2048];
	private double[] cache6D = new double[8192];
	private int offsetX;
	private int offsetY;
	private int offsetZ;
	private int offsetW;
	private int offsetU;
	private int offsetV;
	private CellularCache c2 = new CellularCache();
	private CellularCache c3 = new CellularCache();
	private CellularCache c4 = new CellularCache();
	private CellularCache c6 = new CellularCache();
	private DistanceType distanceType = DistanceType.DISTANCE;
	private double p = 1.0D;
	private int range = 1;
	private double jitter = 1.0D;
	private boolean cellular = false;

	public VoronoiGen(DistanceType distanceType, boolean cellular) {
		this.distanceType = distanceType;
		this.cellular = cellular;
	}

	public void setJitter(double jitter) {
		this.jitter = jitter;
		range = Noise.fastFloor(jitter + 0.9999);
	}

	public void setP(double p) {
		this.p = p;
	}

	@Override
	public void setSeed(long seed) {
		super.setSeed(seed);
		this.c2.valid = false;
		this.c3.valid = false;
		this.c4.valid = false;
		this.c6.valid = false;
		Random random = new Random(seed);
		offsetX = PRIMES[random.nextInt(PRIMES.length)];
		offsetY = PRIMES[random.nextInt(PRIMES.length)];
		offsetZ = PRIMES[random.nextInt(PRIMES.length)];
		offsetW = PRIMES[random.nextInt(PRIMES.length)];
		offsetU = PRIMES[random.nextInt(PRIMES.length)];
		offsetV = PRIMES[random.nextInt(PRIMES.length)];

		for (int i = 0; i < 256; i++) {
			// 2D
			double x = random.nextDouble() * 2 - 1;
			double y = random.nextDouble() * 2 - 1;
			double length = Math.sqrt(x * x + y * y);
			x /= length;
			y /= length;
			cache2D[2 * i] = x;
			cache2D[2 * i + 1] = y;
			// 3D
			x = random.nextDouble() * 2 - 1;
			y = random.nextDouble() * 2 - 1;
			double z = random.nextDouble() * 2 - 1;
			length = Math.sqrt(x * x + y * y + z * z);
			x /= length;
			y /= length;
			z /= length;
			cache3D[4 * i] = x;
			cache3D[4 * i + 1] = y;
			cache3D[4 * i + 2] = z;
			// 4D
			x = random.nextDouble() * 2 - 1;
			y = random.nextDouble() * 2 - 1;
			z = random.nextDouble() * 2 - 1;
			double w = random.nextDouble() * 2 - 1;
			length = Math.sqrt(x * x + y * y + z * z + w * w);
			x /= length;
			y /= length;
			z /= length;
			w /= length;
			cache4D[8 * i] = x;
			cache4D[8 * i + 1] = y;
			cache4D[8 * i + 2] = z;
			cache4D[8 * i + 3] = w;
			// 6D
			x = random.nextDouble() * 2 - 1;
			y = random.nextDouble() * 2 - 1;
			z = random.nextDouble() * 2 - 1;
			w = random.nextDouble() * 2 - 1;
			double u = random.nextDouble() * 2 - 1;
			double v = random.nextDouble() * 2 - 1;
			length = Math.sqrt(x * x + y * y + z * z + w * w + u * u + v * v);
			x /= length;
			y /= length;
			z /= length;
			w /= length;
			u /= length;
			v /= length;
			cache6D[32 * i] = x;
			cache6D[32 * i + 1] = y;
			cache6D[32 * i + 2] = z;
			cache6D[32 * i + 3] = w;
			cache6D[32 * i + 4] = u;
			cache6D[32 * i + 5] = v;
		}

	}

	public CellularCache getCache(double x, double y) {

		if (!this.c2.valid || this.c2.x != x || this.c2.y != y) {
			this.cellularFunction2D(x, y, this.seed, this.c2.f, this.c2.d);
			this.c2.x = x;
			this.c2.y = y;
			this.c2.valid = true;
		}

		return this.c2;
	}

	public CellularCache getCache(double x, double y, double z) {

		if (!this.c3.valid || this.c3.x != x || this.c3.y != y || this.c3.z != z) {
			this.cellularFunction3D(x, y, z, this.seed, this.c3.f, this.c3.d);
			this.c3.x = x;
			this.c3.y = y;
			this.c3.z = z;
			this.c3.valid = true;
		}

		return this.c3;
	}

	public CellularCache getCache(double x, double y, double z, double w) {

		if (!this.c4.valid || this.c4.x != x || this.c4.y != y || this.c4.z != z || this.c4.w != w) {
			this.cellularFunction4D(x, y, z, w, this.seed, this.c4.f, this.c4.d);
			this.c4.x = x;
			this.c4.y = y;
			this.c4.z = z;
			this.c4.w = w;
			this.c4.valid = true;
		}

		return this.c4;
	}

	public CellularCache getCache(double x, double y, double z, double w, double u, double v) {

		if (!this.c6.valid || this.c6.x != x || this.c6.y != y || this.c6.z != z || this.c6.w != w || this.c6.u != u || this.c6.v != v) {
			this.cellularFunction6D(x, y, z, w, u, v, this.seed, this.c6.f, this.c6.d);
			this.c6.x = x;
			this.c6.y = y;
			this.c6.z = z;
			this.c6.w = w;
			this.c6.u = u;
			this.c6.v = v;
			this.c6.valid = true;
		}

		return this.c6;
	}

	@Override
	public void writeToMap(ModuleMap moduleMap) {
		ModulePropertyMap modulePropertyMap = new ModulePropertyMap(this);
		this.writeSeed(modulePropertyMap);
		moduleMap.put(this.getId(), modulePropertyMap);
	}

	@Override
	public VoronoiGen buildFromPropertyMap(ModulePropertyMap props, ModuleInstanceMap map) {
		this.readSeed(props);
		return this;
	}

	@Override
	public double get(double x, double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double get(double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double get(double x, double y, double z, double w) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double get(double x, double y, double z, double w, double u, double v) {
		throw new UnsupportedOperationException();
	}

	private void cellularFunction2D(double x, double y, long seed, double[] f, double[] disp) {
		int xint = fastRound(x);
		int yint = fastRound(y);

		for (int c = 0; c < 4; ++c) {
			f[c] = 99999.0;
			disp[c] = 0.0;
		}

		int xOffset = (xint - 1) * offsetX;
		int yOffsetBase = (yint - 1) * offsetY;

		for (int xcur = xint - range; xcur <= xint + range; ++xcur) {
			int yOffset = yOffsetBase;

			for (int ycur = yint - range; ycur <= yint + range; ++ycur) {
				int hash = hash((int) seed, xOffset, yOffset);
				int idx = hash & (255 << 1);
				double xdist = (xcur - x) + cache2D[idx] * 0.414213562373 * jitter;
				double ydist = (ycur - y) + cache2D[idx | 1] * 0.414213562373 * jitter;
				double dist = 0;

				switch (distanceType) {
				case CHEBYSHEV:
					dist = fastMax(fastAbs(xdist), fastAbs(ydist));
					break;
				case DISTANCE:
					dist = Math.sqrt((xdist * xdist + ydist * ydist));
					break;
				case DISTANCE_SQUARED:
					dist = (xdist * xdist + ydist * ydist);
					break;
				case MANHATTAN:
					dist = fastAbs(xdist) + fastAbs(ydist);
					break;
				case MINKOWSKI:
					dist = Math.pow(Math.pow(fastAbs(xdist), p) + Math.pow(fastAbs(ydist), p), 1.0 / p);
					break;
				}

				double dsp = cellular ? hash * 0.0000000004656612873 : 1;
				Noise.addDist(f, disp, dist, dsp);
				yOffset += offsetY;
			}

			xOffset += offsetX;
		}

	}

	private void cellularFunction3D(double x, double y, double z, long seed, double[] f, double[] disp) {
		int xint = fastRound(x);
		int yint = fastRound(y);
		int zint = fastRound(z);

		for (int c = 0; c < 4; ++c) {
			f[c] = 99999.0;
			disp[c] = 0.0;
		}

		int xOffset = (xint - 1) * offsetX;
		int yOffsetBase = (yint - 1) * offsetY;
		int zOffsetBase = (zint - 1) * offsetZ;

		for (int xcur = xint - range; xcur <= xint + range; ++xcur) {
			int yOffset = yOffsetBase;

			for (int ycur = yint - range; ycur <= yint + range; ++ycur) {
				int zOffset = zOffsetBase;

				for (int zcur = zint - range; zcur <= zint + range; ++zcur) {
					int hash = hash((int) seed, xOffset, yOffset, zOffset);
					int idx = hash & (255 << 2);
					double xdist = (xcur - x) + cache3D[idx] * 0.366025403784 * jitter;
					double ydist = (ycur - y) + cache3D[idx | 1] * 0.366025403784 * jitter;
					double zdist = (zcur - z) + cache3D[idx | 2] * 0.366025403784 * jitter;
					double dist = 0;

					switch (distanceType) {
					case CHEBYSHEV:
						dist = fastMax(fastMax(fastAbs(xdist), fastAbs(ydist)), fastAbs(zdist));
						break;
					case DISTANCE:
						dist = Math.sqrt((xdist * xdist + ydist * ydist + zdist * zdist));
						break;
					case DISTANCE_SQUARED:
						dist = (xdist * xdist + ydist * ydist + zdist * zdist);
						break;
					case MANHATTAN:
						dist = fastAbs(xdist) + fastAbs(ydist) + fastAbs(zdist);
						break;
					case MINKOWSKI:
						dist = Math.pow(Math.pow(fastAbs(xdist), p) + Math.pow(fastAbs(ydist), p) + Math.pow(fastAbs(zdist), p), 1.0 / p);
						break;
					}

					double dsp = cellular ? hash * 0.0000000004656612873 : 1;
					Noise.addDist(f, disp, dist, dsp);
					zOffset += offsetZ;
				}

				yOffset += offsetY;
			}

			xOffset += offsetX;
		}

	}

	private void cellularFunction4D(double x, double y, double z, double w, long seed, double[] f, double[] disp) {
		int xint = fastRound(x);
		int yint = fastRound(y);
		int zint = fastRound(z);
		int wint = fastRound(w);

		for (int c = 0; c < 4; ++c) {
			f[c] = 99999.0;
			disp[c] = 0.0;
		}

		int xOffset = (xint - 1) * offsetX;
		int yOffsetBase = (yint - 1) * offsetY;
		int zOffsetBase = (zint - 1) * offsetZ;
		int wOffsetBase = (wint - 1) * offsetW;

		for (int xcur = xint - range; xcur <= xint + range; ++xcur) {
			int yOffset = yOffsetBase;

			for (int ycur = yint - range; ycur <= yint + range; ++ycur) {
				int zOffset = zOffsetBase;

				for (int zcur = zint - range; zcur <= zint + range; ++zcur) {
					int wOffset = wOffsetBase;

					for (int wcur = wint - range; wcur <= wint + range; ++wcur) {
						int hash = hash((int) seed, xOffset, yOffset, zOffset, wOffset);
						int idx = hash & (255 << 3);
						double xdist = (xcur - x) + cache4D[idx] * 0.333333333333 * jitter;
						double ydist = (ycur - y) + cache4D[idx | 1] * 0.333333333333 * jitter;
						double zdist = (zcur - z) + cache4D[idx | 2] * 0.333333333333 * jitter;
						double wdist = (wcur - w) + cache4D[idx | 3] * 0.333333333333 * jitter;
						double dist = 0;

						switch (distanceType) {
						case CHEBYSHEV:
							dist = fastMax(fastMax(fastMax(fastAbs(xdist), fastAbs(ydist)), fastAbs(zdist)), fastAbs(wdist));
							break;
						case DISTANCE:
							dist = Math.sqrt((xdist * xdist + ydist * ydist + zdist * zdist + wdist * wdist));
							break;
						case DISTANCE_SQUARED:
							dist = (xdist * xdist + ydist * ydist + zdist * zdist + wdist * wdist);
							break;
						case MANHATTAN:
							dist = fastAbs(xdist) + fastAbs(ydist) + fastAbs(zdist) + fastAbs(wdist);
							break;
						case MINKOWSKI:
							dist = Math.pow(Math.pow(fastAbs(xdist), p) + Math.pow(fastAbs(ydist), p) + Math.pow(fastAbs(zdist), p) + Math.pow(fastAbs(wdist), p), 1.0 / p);
							break;
						}

						double dsp = cellular ? hash * 0.0000000004656612873 : 1;
						Noise.addDist(f, disp, dist, dsp);
						wOffset += offsetW;
					}

					zOffset += offsetZ;
				}

				yOffset += offsetY;
			}

			xOffset += offsetX;
		}

	}

	private void cellularFunction6D(double x, double y, double z, double w, double u, double v, long seed, double[] f, double[] disp) {
		int xint = fastRound(x);
		int yint = fastRound(y);
		int zint = fastRound(z);
		int wint = fastRound(w);
		int uint = fastRound(u);
		int vint = fastRound(v);

		for (int c = 0; c < 4; ++c) {
			f[c] = 99999.0;
			disp[c] = 0.0;
		}

		int xOffset = (xint - 1) * offsetX;
		int yOffsetBase = (yint - 1) * offsetY;
		int zOffsetBase = (zint - 1) * offsetZ;
		int wOffsetBase = (wint - 1) * offsetW;
		int uOffsetBase = (uint - 1) * offsetU;
		int vOffsetBase = (vint - 1) * offsetV;

		for (int xcur = xint - range; xcur <= xint + range; ++xcur) {
			int yOffset = yOffsetBase;

			for (int ycur = yint - range; ycur <= yint + range; ++ycur) {
				int zOffset = zOffsetBase;

				for (int zcur = zint - range; zcur <= zint + range; ++zcur) {
					int wOffset = wOffsetBase;

					for (int wcur = wint - range; wcur <= wint + range; ++wcur) {
						int uOffset = uOffsetBase;

						for (int ucur = uint - range; ucur <= uint + range; ++ucur) {
							int vOffset = vOffsetBase;

							for (int vcur = vint - range; vcur <= vint + range; ++vcur) {
								int hash = hash((int) seed, xOffset, yOffset, zOffset, wOffset, uOffset, vOffset);
								int idx = hash & (255 << 5);
								double xdist = (xcur - x) + cache6D[idx] * 0.289897948557 * jitter;
								double ydist = (ycur - y) + cache6D[idx | 1] * 0.289897948557 * jitter;
								double zdist = (zcur - z) + cache6D[idx | 2] * 0.289897948557 * jitter;
								double wdist = (wcur - w) + cache6D[idx | 3] * 0.289897948557 * jitter;
								double udist = (ucur - u) + cache6D[idx | 4] * 0.289897948557 * jitter;
								double vdist = (vcur - v) + cache6D[idx | 5] * 0.289897948557 * jitter;
								double dist = 0;

								switch (distanceType) {
								case CHEBYSHEV:
									dist = fastMax(fastMax(fastMax(fastMax(fastMax(fastAbs(xdist), fastAbs(ydist)), fastAbs(zdist)), fastAbs(wdist)), fastAbs(udist)), fastAbs(vdist));
									break;
								case DISTANCE:
									dist = Math.sqrt((xdist * xdist + ydist * ydist + zdist * zdist + wdist * wdist + udist * udist + vdist * vdist));
									break;
								case DISTANCE_SQUARED:
									dist = (xdist * xdist + ydist * ydist + zdist * zdist + wdist * wdist + udist * udist + vdist * vdist);
									break;
								case MANHATTAN:
									dist = fastAbs(xdist) + fastAbs(ydist) + fastAbs(zdist) + fastAbs(wdist) + fastAbs(udist) + fastAbs(vdist);
									break;
								case MINKOWSKI:
									dist = Math.pow(Math.pow(fastAbs(xdist), p) + Math.pow(fastAbs(ydist), p) + Math.pow(fastAbs(zdist), p) + Math.pow(fastAbs(wdist), p) + Math.pow(fastAbs(udist), p)
											+ Math.pow(fastAbs(vdist), p), 1.0 / p);
									break;
								}

								double dsp = cellular ? hash * 0.0000000004656612873 : 1;
								Noise.addDist(f, disp, dist, dsp);
								vOffset += offsetV;
							}

							uOffset += offsetU;
						}

						wOffset += offsetW;
					}

					zOffset += offsetZ;
				}

				yOffset += offsetY;
			}

			xOffset += offsetX;
		}

	}

	private static int hash(int seed, int xPrimed, int yPrimed) {
		int hash = seed ^ xPrimed ^ yPrimed;
		hash *= 0x27d4eb2d;
		return hash;
	}

	private static int hash(int seed, int xPrimed, int yPrimed, int zPrimed) {
		int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed;
		hash *= 0x27d4eb2d;
		return hash;
	}

	private static int hash(int seed, int xPrimed, int yPrimed, int zPrimed, int wPrimed) {
		int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed ^ wPrimed;
		hash *= 0x27d4eb2d;
		return hash;
	}

	private static int hash(int seed, int xPrimed, int yPrimed, int zPrimed, int wPrimed, int uPrimed, int vPrimed) {
		int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed ^ wPrimed ^ uPrimed ^ vPrimed;
		hash *= 0x27d4eb2d;
		return hash;
	}

	private static double fastMax(double a, double b) {
		return a > b ? a : b;
	}

	private static double fastAbs(double f) {
		return f < 0 ? -f : f;
	}

	private static int fastRound(double f) {
		return f >= 0 ? (int) (f + 0.5) : (int) (f - 0.5);
	}

	private static final int[] PRIMES = { 4147597, 3139517, 7974821, 445019, 7045699, 4186409, 7880549, 5929967, 3607841, 2347591, 2539567, 5606807, 8770841, 4961791, 4017179, 6389737, 8334721,
		6784697, 4344391, 5928031, 5795161, 9856349, 7153457, 2643079, 3313291, 6378397, 2936183, 7841567, 5231263, 8011921, 4921597, 9256963, 106877, 3852139, 7487131, 9128003, 7282679, 8155453,
		385079, 6232411, 8714857, 2584999, 8026631, 9568271, 2932429, 9530767, 123083, 6748531, 3262507, 9414533, 7677413, 3385201, 2061503, 2127659, 3688801, 6103099, 4939349, 8743771, 9115907,
		9027043, 7525579, 3674849, 8138789, 4611193, 8327563, 908879, 2412209, 3374731, 9442193, 9743477, 3105071, 7702363, 8518847, 3918401, 9500941, 4469987, 5805973, 7964339, 6303761, 5141137,
		5132957, 3708797, 694277, 4617433, 4843567, 4881827, 2361727, 2609791, 2646613, 4035397, 2878021, 4676201, 7991777, 8341523, 6690829, 4881853, 8958743, 5564563, 2417743, 5298019, 2728043,
		7358411, 7153873, 5713399, 6300083, 4824011, 4892189, 2517121, 2324351, 2760559, 2794723, 6067393, 7362637, 6159161, 684373, 7789879, 7915361, 8868887, 6037909, 2222141, 4907437, 8057191,
		7717063, 7176527, 8689463, 2848597, 5498257, 2335561, 2306639, 6435491, 9099443, 2001191, 7778731, 9228311, 3073921, 4213073, 2215417, 6654173, 3110231, 5152783, 4783391, 5760091, 8109679,
		4863013, 3486059, 7646501, 4791223, 8474903, 3371461, 708839, 3313489, 810269, 7699849, 5587261, 678773, 4681661, 2045891, 9451487, 7150747, 5023511, 881729, 9508153, 7538147, 8761169,
		9712687, 464879, 5708203, 9367181, 3523447, 2162339, 8142791, 8323363, 5019997, 4599269, 5912983, 6527177, 5896139, 2789729, 8232347, 3822653, 7265899, 5324149, 3376739, 4538999, 9856471,
		9793969, 6310411, 4592233, 8923309, 2623571, 9688981, 7471099, 3267763, 4083509, 7460779, 236143, 8654897, 857953, 5211259, 3161407, 4690711, 3157541, 3944509, 3797683, 8167373, 8205103,
		2959949, 7014979, 2419493, 8860793, 250967, 4517399, 8193407, 5609141, 7319251, 8825591, 3081937, 3080347, 8509301, 3824441, 6080149, 9818873, 9546701, 2672053, 3462289, 6950159, 7976923,
		749347, 2755541, 4976899, 351779, 5475199, 7874627, 6455263, 2146511, 7624081, 5830373, 6483643, 2207987, 7980029, 9968771, 2758517, 8838637, 3111727, 4323337, 2671883, 4488817, 8450899,
		9570943, 3914699, 4592089, 6864059, 9744643, 2435443, 4161349, 8519051, 8115857, 3616079, 6014627, 9838277, 3382307, 2892341, 5871823, 8099771, 4367329, 2090989, 5776811, 4867259, 3978749,
		9156577, 9942421, 6450757, 2655899, 7918951, 4586587, 7319713, 2949319, 9367207, 8698871, 2240213, 8477389, 6980227, 5513191, 6513973, 6122791, 6274049, 9111497, 9639589, 9098773, 2215313,
		4172551, 2919031, 3211123, 7948373, 2699803, 7632607, 8761531, 5129153, 5414593, 8665889, 5503339, 908851, 3583033, 4443041, 8859589, 3009577, 4336973, 4279213, 4193939, 9176147, 5773139,
		7080937, 8873639, 4051469, 3562789, 2718059, 3692291, 809023, 424079, 4706711, 5325721, 4334329, 7589383, 6459701, 8200679, 7272911, 6010549, 7340621, 4781531, 736717, 2978033, 6706351,
		2088623, 3785401, 8885959, 6024107, 3485641, 5105531, 9546931, 7019893, 9588701, 6343409, 8577193, 431833, 9145739, 4680677, 9657881, 5381261, 9193229, 9086963, 2181419, 8278709, 4772837,
		6382793, 8221261, 3850013, 6434767, 499633, 802933, 3839923, 7058609, 4141847, 5202907, 6643361, 526159, 9543173, 8724943, 2435011, 5323693, 397099, 2264623, 3348481, 4741333, 2781169,
		7627153, 3223261, 2253397, 5859017, 5299871, 3478903, 8937701, 4027703, 8757473, 2791379, 5189069, 922451, 9886091, 2547113, 7168853, 2398189, 8183507, 2720603, 8477701, 4668787, 3967237,
		7493701, 6030113, 6434713, 9163639, 4211101, 9238637, 6885481, 7638173, 2146303, 9854743, 6867247, 3331171, 8798459, 6713039, 6103819, 4372909, 558287, 4548311, 4163743, 2065997, 6948701,
		9775949, 5557847, 3301237, 5770601, 2088487, 7714697, 3239681, 9755849, 7857403, 4019833, 8120677, 3853163, 9303467, 5255251, 3338537, 664529, 88741, 4055423, 5014379, 8706983, 792613,
		4704023, 8391737, 3420107, 7272887, 5930179, 2144489, 8535613, 3497873, 3674303, 8745101, 456047, 9672137, 6884051, 6953819, 9083647, 6658667, 2701427, 5790539, 4915277, 4832797, 9997997,
		3334943, 2639213, 5740159, 4209911, 868337, 6160117, 898523, 3155959, 964133, 8100359, 9095491, 4122179, 5774143, 8160851, 797051, 825509, 2841523, 9490963, 7902997, 6325967, 234863, 8559713,
		8702459, 366521, 4979903, 3845753, 9930443, 3044597, 9208229, 2493563, 513593, 2353723, 3065521, 8573519, 3945457, 9039781, 9645173, 6976939, 4985819, 3186347, 9200171, 2475709, 6690559,
		2245637, 170189, 113363, 2725529, 8692339, 90019, 9279071, 5807119, 384611, 8851529, 4488683, 4512811, 7171741, 2248787, 9999463, 3527827, 9203357, 8069153, 4733269, 988681, 7773431, 937693,
		2914363, 2458333, 6814361, 5349083, 6748333, 109597, 7540277, 9666313, 3819617, 132499, 2815331, 571397, 2184071, 6070159, 2685077, 4424261, 6812231, 5657441, 6044399, 4283131, 8671427,
		7758671, 6529651, 6721861, 4490999, 2004661, 9936071, 2562689, 3154339, 7489357, 5489129, 9177107, 8844191, 5740213, 9558971, 2809019, 3462637, 9421411, 3149467, 278347, 2397961, 8534627,
		5693033, 7250323, 9835963, 9541979, 6924091, 8109001, 7825889, 7174529, 4854181, 8041499, 7963343, 6825737, 3038653, 608429, 7822741, 9275807, 3608849, 664123, 9340843, 386521, 7226693,
		9508643, 4721531, 5543387, 5675191, 3101897, 2841463, 6722801, 811981, 940871, 9085007, 377623, 9586747, 3960029, 2544383, 3228983, 7631969, 2650399, 253661, 387109, 6399391, 2580959, 9368911,
		9702247, 6603979, 406981, 5632021, 4502207, 2230691, 65029, 5553739, 6243943, 5898509, 5619487, 9177143, 9847889, 8011727, 2050721, 2530261, 3709939, 7681169, 9520747, 531857, 3777503, 367673,
		5598013, 6241913, 2184769, 8913677, 6811891, 5664083, 4833379, 6604699, 239297, 4678727, 9399967, 569141, 6307793, 2881699, 4123253, 9926611, 2434447, 8473873, 5124499, 8848187, 7515757,
		2260483, 3493349, 6719113, 4124257, 4889263, 6252101, 61151, 9811427, 8317691, 5775841, 7320121, 7560439, 8941519, 732967, 9735197, 7431889, 4046117, 2671583, 7003769, 3207251, 2205883,
		8860301, 3095329, 4583933, 4094117, 3449437, 5221889, 4539569, 3340153, 5475557, 7594501, 2842459, 7850333, 267233, 9113903, 170207, 8178769, 9871973, 7452499, 5094403, 7341871, 2124229,
		6708017, 5257883, 3712369, 8132849, 9182627, 3200149, 960217, 5489717, 2615243, 8727601, 6810907, 7877803, 2321243, 7485941, 7643539, 8647501, 8078899, 7863311, 2818513, 4797883, 8761429,
		3389233, 6169027, 616129, 7270127, 7143569, 341219, 5494459, 221671, 2991899, 4896191, 9001199, 3454769, 4708853, 5549591, 3761501, 8387993, 2899349, 8067113, 7010033, 9674369, 4117031,
		6549619, 7109849, 3418997, 7066777, 3155221, 5504497, 855737, 9491819, 610703, 7039897, 2557649, 785731, 5369387, 7383631, 6181841, 5051447, 8748979, 5365769, 159437, 8468279, 4180819,
		7965547, 2781329, 8177513, 2882447, 5750443, 7350527, 9270473, 9519413, 7423307, 5261699, 3543763, 5275271, 945799, 428801, 7916891, 8653669, 9993289, 8604269, 8139211, 8885801, 4168889,
		6251351, 7854433, 2223581, 2595323, 3704059, 6959369, 8394017, 382727, 8229407, 3492787, 695867, 146099, 3450611, 8959207, 396647, 5881319, 5123219, 4844909, 7063709, 9348481, 3167513,
		5262821, 7499039, 3661703, 3957743, 4844179, 3249667, 2304451, 4604497, 842581, 8852177, 631679, 7630891, 6184261, 2665391, 2274163, 7029809, 4086377, 5183687, 7516361, 210601, 5777743,
		7297123, 7990813, 5100167, 4488739, 3062291, 7589833, 8134127, 7575559, 3725017, 6489487, 2416301, 8161661, 6031513, 5512733, 9762721, 2684989, 9632113, 77137, 8882777, 4774993, 4128409,
		4822421, 9589681, 6253859, 3796097, 9241853, 254537, 8929231, 9800471, 348763, 8062679, 6356551, 5098117, 309899, 5094673, 8652401, 6590789, 6406457, 9746903, 151247, 2617267, 3716641, 412109,
		2036137, 4558769, 4911931, 4481003, 8648543, 2241271, 7704289, 6050567, 2116123, 2080777, 7012949, 5147963, 2761477, 4077041, 9363103, 2556503, 8028577, 6741619, 3185437, 9636083, 5354581,
		5257723, 2907899, 2177429, 693607, 825107, 9100813, 6497129, 845261, 631529, 7580357, 8555623, 5661247, 4691213, 3821351, 8435887, 3767887, 4761469, 409597, 3521519, 5110141, 7539877, 7986553,
		9225467, 9825001, 9200963, 6073451, 4278431, 9592669, 8139421, 3752569, 2475679, 6572297, 4225237, 7223087, 2266717, 7340687, 3248251, 8995981, 2831449, 6090613, 7420591, 5044667, 849223,
		9026863, 6307739, 3443621, 4658713, 9004019, 5634809, 529423, 2413223, 289957, 9123251, 4924313, 9835829, 8557421, 9405763, 140603, 7579441, 2423137, 5653111, 5343703, 5565101, 2305111,
		149087, 9100687, 3519361, 5961869, 4481461, 908113, 4616509, 7826227, 5147273, 9951299, 7872883, 6744847, 5957047, 364291, 6842741, 7308047, 8832403, 3652669, 3303319, 2152957, 2836961,
		2910307, 7530239, 5477489, 2444359, 5618377, 7157299, 535663, 2737793, 2888467, 2007067, 2153729, 545773, 953483, 4184573, 6736519, 9865903, 7588891, 2467307, 3524363, 3431123, 7530893,
		6383549, 2333801, 2052977, 6407563, 8008631, 3595451, 3847673, 6775781, 94253, 8155349, 4639081, 7263401, 3986953, 4933171, 2764207, 3574157, 935213, 7087697, 6316631, 8570987, 3451379,
		3511513, 3363023, 7213391, 9870551, 2757817, 3416999, 3132823, 338717, 6528397, 4141849, 8193079, 7857953, 477209, 9096643, 2767529, 962077, 7287601, 8586913, 8470543, 7827637, 5912861,
		6743129, 2917457, 2910209, 8712751, 5387561, 4813297, 3500999, 3134893, 3950753, 3944669, 4833211, 8569273, 3337969, 830267, 507713, 7032959, 124181, 2409389, 5936891, 6140419, 443561, 395749,
		8916629, 7875697, 3724169, 4880329, 4775447, 5372443, 9403991, 3660497, 7333831, 2036017, 8957093, 6663593, 7002217, 4551103, 285473, 251297, 6500581, 5780923, 7847423, 7325683, 5674351,
		445031, 3408749, 9601751, 4574771, 2512501, 9695129, 6092629, 5290729, 436841, 6390679, 2017783, 2909161, 6073843, 5658073, 9103307, 8504477, 3652063, 4184647, 9640051, 8741779, 3592889,
		3935471, 2861767, 7843739, 5271781, 4266853, 3350803, 3886171, 9765517, 9191437, 5489041, 8827823, 8597969, 6254359, 3551213, 8696711, 4183111, 9696493, 9639401, 2365313, 494759, 4643633,
		8566697, 6876601, 9406003, 2816651, 897469, 2599867, 4987607, 6181919, 163151, 4954619, 2211647, 2802967, 3825457, 2172113, 4798481, 5961049, 7736621, 9858469, 2066693, 9255023, 9587863,
		5968621, 4608301, 8253029, 9204757, 7877981, 5902649, 5724683, 6386041, 8198087, 3934519, 7605713, 4046953, 2957243, 59611, 9706171, 8256019, 2586917, 3755153, 4780823, 5480201, 4043341,
		6599363, 630473, 4185481, 4624261, 7828553, 8370133, 2666303, 8910067, 3503083, 7144021, 3568597, 4368593, 7687607, 2678597, 8704279, 7777849, 8282959, 5288567, 6639547, 6316589, 3351233,
		2104363, 5218417, 95213, 4899331, 9496621, 3733363, 6196607, 2663057, 4315243, 5204077, 2808959, 2672599, 252283, 4971553, 9428147, 669937, 9083783, 322249, 7928059, 6211867, 3816913, 4518883,
		5450257, 5461999, 2995081, 2332427, 168481, 5643329, 7874261, 976477, 8812411, 7954937, 960941, 288979, 5073031, 5927837, 904873, 3487843, 2903603, 3477041, 5361773, 2590547, 824497, 8782321,
		3541919, 8631823, 6582841, 8151487, 7163423, 5047643, 5356129, 2059273, 2752219, 6186959, 6350549, 3164549, 9011939, 3220627, 3164543, 7349539, 4206227, 6047843, 6211019, 5235383, 7210403,
		3289691, 991741, 2664479, 8968241, 7215979, 5872417, 4703843, 5079667, 6207119, 101839, 2518319, 4520371, 2201137, 9539623, 5510699, 6233257, 3374911, 2568901, 2052331, 2983153, 3538519,
		3797501, 8646503, 5933693, 72431, 6657461, 6118591, 6542959, 9742699, 9677923, 7042781, 717593, 4208489, 5863751, 2598217, 762227, 5915957, 6505783, 8764277, 2091227, 999307, 2410271, 5704189,
		5085013, 6801703, 7264277, 4885607, 5432743, 6764111, 761417, 4237241, 2202929, 5326771, 779543, 6568519, 8300273, 6096751, 4376929, 8800357, 6531139, 983597, 6003079, 6374213, 6537337,
		4359031, 2827393, 7971617, 2998483, 5579461, 4011737, 7823773, 5115709, 7613981, 5792333, 260417, 6131507, 3281767, 832379, 5707309, 5784323, 7507579, 984923, 6979961, 4755979, 2165237,
		2628167, 8531261, 9506317, 4505131, 268439, 5330947, 9366919, 8882843, 9135757, 7276403, 488633, 4862989, 2040457, 414697, 4661039, 9309379, 78649, 5012347, 4752931, 2946929, 3172567, 5761909,
		5880757, 5955923, 9191773, 5416199, 7715291, 9346397, 2509891, 9113869, 9134747, 2606833, 7486981, 3567019, 3190909, 6593941, 7446863, 781409, 5516689, 7863253, 9322627, 9881449, 3753593,
		9949487, 845753, 4636963, 85193, 6243287, 4419907, 590131, 6150113, 7818379, 3312163, 89459, 9692443, 70229, 2263931, 5739073, 5321737, 7866169, 6410681, 5655211, 9374033, 637603, 6992651,
		6461779, 7658467, 5439163, 5919691, 3332267, 5282677, 5307787, 429349, 2590717, 2753291, 2953793, 9739747, 8478109, 3153497, 306157, 2104147, 5263891, 7826207, 8894947, 2676301, 5485351,
		4517957, 8491073, 5540683, 2118343, 5154307, 9817727, 9070063, 8276833, 5658119, 6775157, 4513273, 220709, 7408333, 5549711, 9097591, 218599, 8388187, 540611, 952933, 7347437, 3297449,
		4988927, 120299, 3035723, 6659267, 7585397, 550973, 6211223, 6580817, 4916773, 7998533, 5372417, 8819999, 5412391, 8105333, 5093609, 121867, 5270203, 9966389, 9740659, 3526111, 7038181,
		6035501, 5084851, 2718017, 6438881, 6188341, 2103653, 3280493, 8905957, 7173743, 6634673, 6553297, 2465053, 6561287, 5122669, 2945207, 2693989, 3812119, 9908081, 580663, 8165401, 556687,
		8466091, 7770227, 5791673, 527251, 8224301, 8006549, 4115239, 9829409, 5553059, 9607657, 3755083, 7809757, 7309567, 4957049, 6567887, 847097, 8548681, 5043967, 3325373, 4520069, 7083707,
		607531, 9734051, 2415319, 5455763, 6041491, 6202991, 5540879, 6324677, 7242943, 4863769, 7782167, 9281521, 2814751, 6559279, 688679, 757381, 6002669, 5777323, 2826683, 9756107, 4649341,
		468821, 4582421, 2833093, 5411381, 7010693, 8368079, 5121841, 6887927, 6437051, 3378203, 7870409, 4742063, 5772311, 3677951, 293893, 2708263, 6086383, 8075203, 9035729, 3247921, 2732897,
		2036579, 6527047, 5287181, 5501401, 7141657, 9610141, 3519821, 562519, 5369627, 2876789, 7332583, 2619937, 5426189, 5108153, 565771, 9369221, 7924487, 8957129, 7783927, 8216573, 772567,
		961733, 986239, 9520081, 4668949, 3069263, 6660109, 5355797, 4251239, 574543, 266933, 8807219, 714139, 2723353, 5444323, 6271301, 9703367, 3347273, 3585787, 7576757, 4331051, 6455167, 9448067,
		2838281, 5384039, 9384143, 4665893, 7067303, 2292469, 696961, 4203791, 7583083, 3212173, 3322379, 3840253, 6969673, 9513197, 2835401, 2951933, 2745661, 182029, 4285769, 7505543, 2787149,
		8179151, 6706253, 9116339, 6259727, 5357423, 2892053, 3753781, 4108457, 5939333, 4362331, 6367409, 3026369, 5861321, 6880051, 8549201, 699383, 5507209, 4731479, 214559, 7089319, 5345359,
		5274163, 5331559, 6314051, 138179, 265079, 7716721, 9362789, 5407091, 3575317, 5545273, 2680697, 8607029, 3738733, 415447, 9024277, 2110553, 2885863, 299477, 8400853, 5271107, 9478559,
		9169351, 9101623, 786959, 3706529, 5672189, 3930709, 8954831, 3155017, 5515457, 2995973, 9945937, 3699947, 4364147, 5260597, 2278631, 5725697, 7237453, 4033411, 839207, 657469, 5333429,
		6922129, 4357139, 3812923, 8803819, 940981, 4840441, 7589863, 9855647, 2805923, 2628953, 5544767, 4663331, 683699, 7049971, 7963099, 7098859, 4903949, 2611979, 2912051, 8875249, 7915181,
		730321, 8131159, 5496919, 6850693, 3108793, 5683087, 5535037, 262369, 140677, 4057909, 5551627, 4949449, 9761737, 3937061, 9910331, 2918767, 6767017, 3555767, 7541311, 4396111, 9031051,
		3274511, 4545209, 3901757, 2274841, 4063957, 2127299, 9825107, 5188697, 6531323, 3202333, 8540989, 5646379, 8304889, 9412889, 516541, 6845659, 9781909, 2587147, 2322367, 8021011, 6154531,
		6246197, 4143647, 2164307, 2740667, 703559, 5997689, 9139769, 7490023, 6661141, 3794257, 2645101, 5036261, 5866243, 6618809, 3940159, 8861851, 2603897, 6525787, 4431311, 407587, 9798079,
		5091847, 5280521, 324209, 2695789, 512761, 4128601, 332039, 4011347, 7047737, 5464891, 3578473, 7420243, 2946479, 5583199, 2335967, 577169, 8322329, 7842713, 3241253, 7134583, 4544233,
		4661429, 7606387, 978079, 8666069, 7767533, 3041383, 3382013, 5064091, 3677129, 2193469, 5766991, 5837173, 4321781, 9749393, 4620589, 9501287, 777031, 3992683, 444979, 6820993, 6346463,
		4591183, 3351097, 9502663, 4540189, 8273791, 5103929, 268897, 3512309, 7199807, 4120211, 8912689, 2904287, 8518183, 7381411, 3696761, 6107599, 6831239, 6881507, 7738559, 4371847, 9779537,
		3441989, 8267003, 340211, 6899069, 4564793, 6618751, 2839171, 3789257, 9057313, 6300563, 8079481, 8605469, 5661389, 9987071, 374069, 2365169, 4824439, 439969, 3527239, 3010597, 6139579,
		236549, 763921, 7229707, 4591163, 5010781, 4982221, 4833209, 176899, 4829219, 9709313, 6171829, 6688301, 3647339, 4958251, 5056339, 511603, 7633253, 7747673, 4118893, 8628847, 8041589,
		5686427, 3178321, 5645137, 9809741, 6532733, 650599, 5787107, 8910557, 614687, 7050557, 2747959, 8826953, 8245649, 5040493, 5215097, 7166209, 3704573, 238037, 112901, 3436253, 4180373,
		6503557, 70849, 9579187, 7216927, 5463389, 7929269, 7304009, 7417909, 3612923, 9335759, 9900091, 7434223, 5878489, 2939869, 6726397, 632117, 7407349, 4236989, 4057019, 6597397, 4184099,
		2163467, 6068477, 4299637, 5547019, 6021263, 271841, 5635453, 3368041, 8492569, 8257219, 5741369, 6361879, 2290703, 5994041, 8817437, 4315027, 6732373, 9365177, 6554621, 6792211, 4371649,
		5259047, 9309413, 2186087, 7320721, 8627701, 6965099, 7255357, 2129807, 234893, 9664091, 8110253, 3160583, 6457489, 2620921, 6158501, 9238277, 4470923, 6620441, 6958111, 8183711, 5918933,
		536677, 6760909, 8974481, 8718709, 6000283, 3586859, 318883, 5560589, 7437709, 5210651, 6787747, 5852153, 9378931, 289897, 3486559, 4777807, 7372229, 83701, 4294919, 6071797, 4069687, 3816539,
		7599841, 8138371, 9085337, 4931749, 6749329, 6821911, 3245153, 3452413, 6345481, 4803719, 6727783, 8952683, 6038203, 4862107, 3923867, 8963551, 8573827, 980831, 378683, 2207861, 171947,
		2239049, 5414963, 5236597, 6737131, 8069459, 6164293, 2127401, 3798349, 5815811, 644401, 9602563, 4835153, 55997, 6851639, 5203069, 4529989, 6538309, 4267579, 9075917, 7708991, 225163,
		7610521, 8185237, 3006781, 5582351, 2080277, 875243, 7307543, 9455917, 8548469, 3475601, 3296707, 443791, 7736653, 4039681, 2793793, 4179667, 425083, 8678777, 2529251, 403849, 7082807,
		5158261, 7436447, 395543, 843779, 5347291, 2302169, 7614647, 8622883, 5585989, 6059197, 8646853, 2726441, 2127043, 189229, 3779333, 3973483, 911549, 5052533, 6269969, 9844283, 61253, 7684939,
		8997427, 8605829, 3726823, 3178907, 7309639, 3865801, 4057199, 6011933, 3298279, 111091, 2262857, 7588093, 7282151, 2762003, 8308033, 2251903, 3590437, 74521, 4224037, 6639067, 5042903,
		2215141, 5657749, 8690953, 762277, 8100217, 7168297, 4930649, 7627981, 2822791, 7266653, 6317963, 3461233, 3257533, 4928953, 7231669, 4593157, 4092043, 8635867, 3212381, 9952559, 2571281,
		8821919, 4528087, 2640689, 5476039, 3115597, 2526827, 9011551, 4668451, 5493899, 6452801, 5596147, 8050261, 6366649, 122761, 2506073, 9938531, 5968181, 4058843, 2708611, 875393, 6786887,
		9110627, 6506881, 262303, 8219899, 3570223, 8366131, 3494483, 2641543, 9383021, 7156783, 4806127, 9312899, 2859173, 397259, 7144327, 5654819, 8067751, 6229183, 9873679, 8329093, 6257057,
		8846291, 5598469, 9978961, 4075231, 652741, 9604379, 852521, 3767809, 4379021, 614773, 3334789, 6172213, 9668557, 7515367, 4725739, 5015033, 6018317, 8930833, 111191, 3406981, 2430257,
		3200081, 3236243, 8693327, 4806181, 2042171, 8257313, 7207223, 8561551, 8647393, 4086529, 3184919, 6027949, 2943427, 5679677, 5595427, 83717, 9741713, 9838033, 4057769, 9311681, 3315173,
		883783, 3766109, 9745597, 6545621, 3807907, 5207731, 2321677, 7623013, 7917187, 4691521, 2916943, 7445369, 9449933, 8155267, 4542283, 6679369, 8789251, 7857481, 950791, 2088587, 9821137,
		3242047, 3560393, 5837719, 2999263, 7410727, 6156659, 6287173, 3901867, 299333, 2759293, 3709301, 2710943, 2152669, 560489, 8875829, 8235067, 3945607, 6955073, 3048833, 7122331, 2563819,
		3591083, 7444193, 6533561, 9688099, 671557, 5215997, 8691281, 4375621, 7755353, 3093947, 9318511, 194003, 5486449, 5070829, 7787123, 4919701, 9407117, 2000731, 6174913, 4765529, 4569613,
		8174021, 5087413, 9542453, 2152571, 6536141, 5920279, 2222683, 8966591, 683983, 4069837, 7217557, 9716807, 7764851, 5025521, 623977, 8351669, 6025577, 8124161, 5293679, 3827869, 2673977,
		6468317, 5933437, 4535159, 539633, 6144989, 2348371, 947033, 9555527, 140869, 102547, 4454353, 5566559, 4747343, 4983523, 9368999, 962743, 6353707, 8437421, 9980153, 384343, 3510553, 6678667,
		8274131, 3984791, 5541311, 9253397, 8307487, 2397041, 4807109, 3798581, 7643227, 4779871, 9069349, 4572679, 7960481, 7710761, 4784303, 9376573, 4304137, 3883427, 7089637, 6362711, 5567467,
		2166127, 630163, 4878589, 3394411, 9087667, 7296089, 4862911, 431707, 9192389, 4872451, 832189, 256057, 8885761, 2603581, 8491969, 7544491, 6335983, 9053897, 9765181, 655559, 8876479, 5651741,
		7102127, 774901, 2872003, 6639449, 6428447, 2928287, 3767411, 3786347, 8038313, 3851797, 3619813, 5802271, 9222547, 4608899, 159287, 104053, 7550953, 5073517, 2504323, 8565209, 4374767,
		2027021, 4295371, 961769, 8849681, 2086907, 9237461, 4569217, 9742483, 5851969, 7213603, 2948681, 6609457, 6497203, 676349, 8553929, 8764253, 4463317, 6889853, 7020623, 502133, 2202997,
		8009039, 7698269, 2585357, 7154687, 192971, 8438761, 5301781, 505877, 6832003, 9192661, 9365297, 5712337, 2983819, 7320197, 8106529, 6441439, 5390503, 6917821, 2139407, 686039, 6863663,
		6741487, 6518753, 8287361, 6504583, 2266973, 2143261, 8934869, 3070601, 2163137, 2414207, 2095997, 8233139, 739909, 3899867, 9470693, 7265317, 2536361, 2488121, 9497557, 2824333, 6815293,
		5233139, 886069, 9487073, 2692787, 5768599, 3861929, 2663461, 7282619, 2751379, 6383939, 8395043, 3838397, 7449187, 3374909, 9962003, 5181383, 6068549, 4643047, 2156599, 4096441, 2782009,
		6681781, 3079613, 763429, 9379261, 6698009, 4331009, 4668527, 9247577, 9333113, 4891091, 5673653, 4438597, 3927271, 3985867, 5794273, 3454387, 6896707, 87553, 3503393, 8404283, 9225361,
		6632519, 7467197, 9271091, 9195899, 408131, 5754887, 4337999, 6324611, 274583, 9014479, 9768637, 5046079, 6331823, 8645431, 915587, 4845271, 5715011, 2284589, 6009571, 6382529, 5271283,
		7123199, 2502391, 5422189, 4324907, 3758107, 4192697, 3725027, 4891153, 3085067, 8599397, 9923731, 4072301, 4627039, 5381899, 3737731, 9061763, 4224811, 2827621, 5650691, 5716871, 3405601,
		6248149, 7125973, 6524981, 4155797, 5104991, 5854729, 6770807, 5154823, 7643593, 7006319, 9810971, 4408687, 3389891, 6086831, 5021977, 9401837, 7856867, 7722877, 80141, 122597, 2246273,
		8856559, 8771599, 8619601, 5959813, 2469479, 819001, 6502241, 3115621, 783019, 5412703, 6825251, 906263, 9147871, 9699203, 7049453, 4415503, 3628007, 2093557, 8990239, 8370959, 6045797,
		9127061, 3875873, 6254867, 8744467, 2888339, 6497261, 8368831, 2379973, 3386797, 8639773, 325693, 8768909, 3089027, 9889849, 7325191, 391907, 8031797, 4598801, 5466397, 7275857, 8243869,
		337453, 998471, 5852657, 7815527, 6775693, 6267133, 3386311, 8968103, 3236531, 5596489, 2440331, 7912103, 3338329, 2868871, 8378857, 8120251, 8344601, 8478037, 2317873, 5468893, 7587383,
		3831929, 9178769, 7566193, 417239, 7466831, 3884099, 6292541, 7085437, 8114453, 3813647, 8918333, 5030239, 7103339, 5038801, 6430261, 8770451, 9523421, 2445283, 814631, 2858129, 4099049,
		3484177, 78439, 2428529, 5606329, 9641173, 4834553, 844513, 9388207, 2369123, 7364033, 9091843, 2667983, 4494701, 9720371, 482021, 7298827, 2639431, 5876137, 5879681, 6635051, 8354999,
		7108687, 7361429, 8714039, 9926737, 4944281, 351847, 5548999, 2512403, 2185861, 7145473, 5207803, 4057861, 2428033, 7368709, 5818847, 8045897, 6136993, 9023827, 9187901, 5616379, 7500547,
		2441377, 4098187, 512147, 543871, 2129399, 6242503, 2555353, 2793269, 8644261, 219941, 4864889, 9771023, 5820499, 7641479, 668513, 6027719, 9718573, 4163251, 2790871, 6602023, 450797, 4512553,
		6741223, 7974677, 3729853, 8178109, 3491129, 7560493, 4594999, 5154641, 825439, 2133191, 141863, 6325903, 2574977, 9012131, 8419087, 5068891, 3111653, 307687, 88903, 9386911, 6758303, 5514571,
		2019487, 3310187, 9518533, 7851719, 2937167, 6736343, 8667973, 2352079, 5387413, 7738777, 194749, 9891439, 5087591, 2410417, 9804793, 3234017, 9779593, 7878139, 9321127, 3101557, 8826197,
		4416047, 4367411, 7021979, 680327, 6742249, 8655611, 834643, 3230483, 2157109, 9450073, 775861, 6065483, 4693753, 6339451, 5404381, 6026071, 4896803, 3288169, 7090597, 7027931, 5315473,
		4705159, 6395971, 6112159, 4916591, 4519829, 780163, 5215297, 4066523, 6414193, 564463, 6492511, 4109507, 5205913, 744661, 4346803, 7944943, 4208261, 2972791, 7039693, 3556271, 8280521,
		8431979, 7851457, 9313013, 685511, 7333321, 4444703, 8628377, 5512589, 7519333, 6437663, 4195861, 2674031, 9601717, 918319, 9852467, 4706857, 713497, 7752971, 4274177, 8639599, 8224219,
		2851423, 5525279, 9853733, 6223201, 8233681, 9625787, 758713, 945677, 2132983, 8569711, 6821561, 2352227, 318023, 6042221, 7599191, 5631707, 2719393, 7545011, 7877197, 8105633, 4636799,
		7989409, 7744057, 9975857, 2183341, 4572391, 2159329, 6045881, 8158109, 4760981, 7965151, 4529383, 2687639, 4699367, 8789309, 3101971, 241543, 828859, 4865929, 9076481, 3486283, 8360999,
		7624261, 8115011, 4653497, 2582491, 6804121, 6719623, 7263989, 3539849, 5556517, 7166057, 9422201, 7260401, 8876729, 3107491, 9221423, 6695681, 578803, 4257067, 4600399, 2817169, 5310763,
		3715793, 87559, 2827931, 338747, 678101, 2715997, 9445091, 8361763, 2501479, 7112927, 5773109, 2913371, 8077247, 9280027, 6410561, 5308001, 6944227, 7420337, 9543269, 120929, 7986947, 5348201,
		2324681, 9082091, 5893367, 6301829, 4181413, 6711667, 761429, 4808249, 7579633, 2750831, 8259703, 3865867, 9981847, 5236741, 2010221, 7211119, 8703829, 5098747, 8640391, 6043001, 4121783,
		414461, 6055541, 715397, 7514543, 6530087, 6034141, 8849417, 7628657, 452401, 7633303, 2329507, 5038921, 7639721, 7267493, 4228727, 7714543, 351731, 6128477, 2822723, 4502653, 3215819, 254249,
		3663197, 6318919, 3221917, 6633197, 6181277, 8472899, 6948923, 4758617, 5887061, 2158301, 8731913, 2862313, 6714563, 4583339, 2797357, 497537, 7153961, 9263173, 7906601, 6870053, 915527,
		5967271, 8370569, 505823, 4359133, 5369279, 6760007, 177883, 5092721, 2942657, 9060761, 6486839, 8858453, 8940479, 5166353, 5804749, 2585447, 4358257, 3574619, 9587707, 8472713, 565979,
		2807089, 3260293, 8697581, 9141901, 8977597, 8147977, 7818731, 5115923, 9269291, 3314837, 8036417, 8610373, 8284447, 7503929, 3474517, 6260251, 4096271, 8461961, 2615999, 3267841, 3173657,
		6469219, 7164019, 9416549, 688411, 3223837, 3800887, 3990997, 6515021, 699931, 272287, 83641, 4171201, 6216311, 3196381, 5055623, 8989567, 8968913, 5259379, 2831971, 2847583, 556399, 847453,
		6777307, 5556743, 8816837, 5655283, 577471, 6025307, 4402543, 9795109, 2072809, 2221111, 9220439, 5345663, 6950959, 162601, 2295569, 8355419, 3882007, 4998341, 9169739, 2902219, 7057013,
		7232627, 2658883, 3460993, 2345401, 9014107, 9878333, 2369779, 5786113, 810583, 8272973, 8288459, 6031717, 4280083, 2236483, 8817847, 3546199, 8555579, 4345651, 5922349, 8380667, 797009,
		4973791, 7215911, 4641191, 5766851, 3010067, 8026171, 8189981, 5926621, 4527811, 3044269, 4420627, 2619691, 4007209, 7683553, 8787371, 9434993, 9477829, 5898521, 9308521, 3411839, 8431627,
		2089397, 8140711, 9955723, 7556999, 9456613, 4017659, 8534423, 8443969, 6965327, 8567341, 2608663, 3576637, 7760117, 3383117, 7470647, 554573, 8652697, 7324069, 4911463, 654629, 3200137,
		6505867, 3350779, 7809941, 6926743, 304559, 3911879, 2308069, 5075309, 8796517, 5980033, 7039849, 7469123, 3804943, 8979379, 3562927, 860647, 3405547, 9548447, 3843107, 962459, 6631901,
		2539519, 6229193, 7639699, 3616883, 3064679, 4211149, 3954971, 7627289, 7035911, 4075909, 4817797, 5843477, 3499831, 607043, 7744393, 4808533, 2602441, 2958859, 3084217, 9186871, 2392279,
		4165597, 6735647, 5642699, 5914409, 3237287, 840743, 6401377, 5848589, 9164047, 7044073, 7667003, 9193181, 8461241, 7727501, 3377993, 2127977, 5406293, 2825821, 8908553, 5379931, 424037,
		2630333, 8797543, 7189939, 4783147, 3599357, 5257103, 7723381, 9239323, 9645451, 9695701, 2262641, 8309687, 5175103, 733741, 5201023, 988129, 7894907, 2033299, 4797577, 5332069, 5102059,
		6488539, 5966773, 5914049, 9676439, 9935257, 7195361, 9154547, 9739133, 67213, 2633131, 6080567, 2196499, 284131, 9941621, 5271703, 8118191, 4740289, 2346977, 3296263, 4987207, 4467767,
		2130703, 5102473, 4189561, 4363421, 6976993, 6658027, 2056727, 3271967, 7002383, 531799, 4039447, 6947389, 72337, 8047649, 9182711, 8855507, 2858041, 7102897, 4507201, 7846649, 8713781,
		7321453, 3281293, 4597027, 2097709, 711287, 2630041, 5338013, 2757061, 8273257, 8425279, 8901751, 3541193, 6355493, 4462021, 8910281, 2485027, 3590773, 6137801, 4373207, 9079943, 6915661,
		9762679, 5265829, 6410797, 2640749, 2535469, 3922553, 9062203, 4847231, 3383951, 5151581, 3017801, 9267521, 7107557, 2117321, 57751, 2271473, 3135731, 8978327, 3714049, 4256653, 9839119,
		7325281, 3791551, 6570461, 4791343, 8297441, 2815739, 4667303, 5090447, 2447369, 9406949, 7680877, 9457781, 5436799, 9736127, 4521679, 7918499, 3227717, 819653, 9577483, 9371863, 9513989,
		9965609, 4610321, 8982559, 4527709, 2190997, 4593637, 5288603, 2860687, 7986007, 5375659, 8693693, 7544857, 8440499, 4264849, 4469879, 9751129, 5878027, 4531321, 6955573, 3557843, 9507703,
		3420701, 7385839, 3639733, 4051181, 3900121, 9804799, 4630019, 3864599, 5271067, 3253073, 7763669, 8182589, 9352247, 7475789, 7203407, 382541, 8669389, 9427151, 4564411, 4917799, 898459,
		8414831, 4053443, 66449, 300491, 5368043, 4369163, 4988023, 2293141, 6676777, 674551, 2805689, 6650729, 6520289, 4423729, 534799, 5366219, 9498023, 3409297, 9518161, 361637, 9074033, 7648637,
		3752779, 5753719, 6594131, 9906781, 4243411, 7625257, 7196881, 6248743, 3720623, 3034979, 4591997, 7630999, 7032383, 8311591, 9315347, 6827551, 5485483, 7602403, 3258161, 5192483, 3877987,
		4677241, 2125043, 9579719, 577333, 7187267, 4154861, 445363, 8543791, 4988411, 8045077, 7293197, 8246477, 7693453, 8547949, 3920941, 3314713, 7527449, 6087589, 8103031, 5340341, 8231303,
		6663451, 2422757, 4265431, 972623, 7338889, 9225107, 2385979, 3380539, 3585667, 9157223, 9641959, 8468353, 561733, 5010539, 3363677, 6873563, 8347267, 3455371, 6517411, 3996583, 9417743,
		8993587, 8812891, 516721, 7325443, 998071, 8171297, 5768857, 9692923, 764209, 8535413, 2379149, 8078431, 6888967, 2251829, 9474617, 7973131, 2597407, 3614239, 9776779, 7017133, 66809, 4952071,
		6091313, 5634869, 4630921, 9057017, 8449607, 3584927, 8058227, 7853959, 3147721, 5046199, 7790161, 7804609, 4819729, 7340527, 2865397, 6991741, 7266299, 3762331, 8191357, 4618589, 5995081,
		3752081, 8034373, 3292213, 8253953, 7598191, 2897611, 512389, 6349531, 6111013, 145589, 7969469, 7805891, 3728393, 7605853, 8294971, 469031, 3044837, 6019991, 2166121, 2735723, 5232559,
		5356697, 3488599, 3747581, 7723907, 3298403, 6971903, 4891823, 230239, 984017, 823489, 6378287, 7902383, 2454073, 9027173, 4545089, 9921343, 356869, 2864633, 2657861, 9761287, 3929407,
		4229711, 8750347, 8553403, 3460417, 6559699, 9340787, 5949347, 7574893, 746807, 3657439, 728941, 862633, 3188089, 5917099, 5440273, 6773047, 9194527, 6446569, 4558549, 9666427, 2972701,
		4379611, 2507581, 4043087, 4709609, 2500543, 6713867, 2130503, 3006511, 3745211, 65033, 2971879, 6144079, 5870591, 7853999, 7802453, 3724907, 392467, 448363, 6098999, 9695471, 9745819,
		9896681, 827633, 327721, 205417, 5749847, 6226279, 4981747, 167381, 5211587, 5105381, 3824333, 8265743, 5683481, 2222503, 2901097, 5296441, 868171, 3875623, 679561, 8434691, 7471157, 8479883,
		2229959, 4354099, 2480441, 5693543, 3277693, 6415457, 789491, 9563507, 6199771, 9259619, 8462497, 9801977, 8113643, 3441469, 795793, 164291, 388009, 2651989, 5048423, 3974177, 9688421,
		4881581, 2640509, 7962151, 7165519, 6275597, 4223753, 4219799, 339583, 4874687, 2258149, 4319681, 2157131, 3464057, 2266753, 2560169, 5300927, 5529767, 605573, 9996589, 3434891, 5180933,
		8285363, 9943951, 3202873, 5889553, 7634657, 7580737, 7061311, 3557977, 4525501, 3315071, 7846819, 2454787, 7627573, 4036933, 4059761, 2212009, 7259839, 6361279, 6086063, 9820403, 5777423,
		3646067, 9554609, 748337, 3951421, 7781297, 4372019, 85223, 5023309, 3631339, 3050371, 379703, 188911, 6065963, 8735371, 9521549, 9514867, 4170773, 6182623, 5903533, 5864933, 6338971, 9504353,
		2132563, 2028401, 5517779, 6751049, 3537701, 7174907, 901213, 586139, 8410229, 6864623, 6115547, 9933241, 4897811, 5003203, 8751859, 9110513, 6777209, 3395209, 7317173, 9672941, 4483231,
		4032317, 2591777, 9053323, 3949013, 204443, 3421813, 4221941, 5862179, 7826419, 2645189, 8392819, 2594981, 9020929, 446401, 683731, 5031329, 460711, 8964569, 4120793, 2950093, 6722593,
		8884453, 6141671, 2209589, 5271901, 3410189, 7774889, 7175239, 8909189, 7470823, 8630903, 811273, 672667, 5729959, 7722277, 9104413, 5546249, 5151781, 7238293, 8830271, 9658861, 6765263,
		509317, 2283889, 8140291, 4153067, 5491883, 5949149, 9666053, 5053441, 615107, 4601969, 6433241, 8321171, 5399101, 3043769, 3918491, 3180251, 7171429, 475283, 9237433, 7260079, 9423461,
		2630921, 4000267, 9523001, 8313889, 7328747, 4319311, 138401, 8496611, 7868137, 8119471, 6906499, 9331639, 5926729, 5773577, 7965007, 9546857, 8594263, 902599, 8089673, 9793451, 7644797,
		4565929, 2269819, 4987589, 3575839, 884267, 3365771, 7851301, 5833103, 685987, 4253309, 4479851, 5682161, 714199, 435343, 6978253, 67433, 9511933, 2358739, 5053681, 2064583, 6319471, 8709191,
		3110153, 6021809, 9753451, 6691277, 3814931, 3040469, 5367623, 9257683, 6264703, 9785299, 7860841, 5600047, 5448869, 6217681, 9727021, 6468289, 9917473, 8880761, 9699461, 5037017, 2034359,
		9996461, 7370431, 7849181, 2602771, 3217547, 5881417, 3948229, 8985253, 262901, 4241291, 7753931, 2386003, 7100677, 6951583, 9500917, 72997, 4229707, 8192299, 7774399, 907667, 3850159,
		5084117, 712021, 713309, 4198003, 2577851, 4902211, 822581, 7522973, 8880073, 4057297, 7447541, 2741287, 9562561, 2026807, 2627519, 625657, 9160913, 8859307, 6804739, 4848527, 7822597,
		4033207, 2565257, 2301361, 7149811, 2746921, 4986167, 2321383, 3390323, 2583299, 4045387, 6058193, 5117713, 3116747, 3695201, 4478017, 9195451, 8554967, 6316019, 7986371, 7133663, 4761661,
		8158387, 400643, 465077, 3187321, 266381, 7808191, 6771217, 170213, 6837899, 6811939, 936673, 8127697, 9386383, 3738253, 3431759, 3675277, 7321129, 5204039, 3378719, 5640367, 4394743, 4692383,
		7850317, 8424289, 8892109, 3952307, 9740737, 8765243, 5467541, 4547791, 2872579, 5977651, 3461203, 4486499, 4586963, 7159111, 948377, 4620877, 7781159, 107071, 5507519, 325453, 868157, 219517,
		8472493, 2750771, 8477113, 9771211, 5270413, 4117181, 444001, 141649, 7195453, 5096711, 5429153, 8647277, 542183, 2002681, 2699299, 88771, 7399933, 4060073, 4948117, 843503, 6654509, 5021657,
		2023121, 4736177, 958039, 7876501, 3753767, 9458927, 2770171, 7100173, 8884147, 7616837, 4482619, 778363, 8215799, 777977, 611833, 6962441, 7810619, 2284027, 5457079, 2854403, 9111581,
		2245301, 8225939, 4431641, 5421161, 6965591, 9819031, 4507021, 848611, 321509, 8390303, 3073181, 5738611, 9396091, 6732689, 7858099, 7323331, 9904267, 9159329, 7044253, 6699311, 5816207,
		3933599, 198701, 6236509, 6529021, 2275067, 776759, 5666131, 9964769, 8053921, 783779, 5593477, 3498919, 326941, 2122363, 5881913, 6221951, 2680109, 8145833, 9176813, 6128953, 5270873, 534019,
		434407, 8661841, 4968637, 2618339, 5546921, 7453723, 9918353, 2995159, 9755077, 3580133, 882263, 9761827, 7874761, 6570359, 5105203, 7827587, 8118791, 9078973, 3888281, 6200939, 6688651,
		6953767, 5604853, 6727993, 5583187, 6007189, 5975089, 2652407, 7309039, 122039, 5731573, 7416583, 4487111, 7236319, 726787, 7053877, 4589933, 6064397, 7879447, 8735891, 6196297, 3228143,
		2166709, 306431, 5400091, 4991933, 2129867, 5399899, 7215121, 9235133, 4796573, 7962389, 7868233, 6934903, 3238597, 735997, 9948311, 7007603, 8013119, 9760367, 4705837, 699211, 9312547,
		4212587, 929791, 8704193, 5131963, 4877071, 6756833, 3077647, 6945199, 6211397, 3618347, 6852997, 364241, 5045291, 228883, 6559081, 3763093, 8140529, 572573, 2294291, 5185567, 6883153,
		6597593, 451709, 9480391, 2646641, 5401763, 2718299, 234203, 9976027, 5376869, 6325051, 8162509, 994709, 3988771, 8810383, 5400299, 2363749, 8937557, 8972863, 3248731, 5188649, 4248757,
		3775063, 5745731, 8196599, 5984711, 219953, 6676889, 5181287, 5506141, 5231797, 8223337, 3673073, 7809433, 6235459, 2644879, 9753179, 5241311, 4582309, 4272731, 602983, 7784701, 5272489,
		338251, 2210963, 8804123, 154769, 6726749, 8571347, 2037829, 574003, 361217, 7557427, 2089267, 4556771, 2973701, 6734011, 7315489, 5621599, 4751807, 4525187, 3999739, 7551461, 4782311,
		8603297, 5824843, 6672563, 5844331, 7394767, 771181, 9078463, 4514513, 7498763, 3250669, 8489197, 9188947, 5241557, 7415483, 9341533, 3325349, 7520621, 3259661, 5343179, 484229, 7598477,
		6143197, 7968593, 4313129, 6802891, 8984957, 629513, 8363261, 2795227, 5569001, 8317259, 4945277, 8169481, 6081409, 5348407, 3354721, 7606913, 2215667, 4427503, 2451919, 3210283, 8791297,
		7380469, 9949273, 2561963, 6050507, 2035973, 9186811, 9039827, 5222533, 8431679, 9846413, 7947941, 8190031, 5984929, 274069, 8743919, 291143, 2320811, 456517, 7105223, 3857561, 2705113,
		6209503, 5701877, 3602021, 3062461, 9806549, 310801, 5639167, 379963, 8582051, 6074447, 9752797, 3065591, 6013829, 2629909, 9278809, 9613759, 9952799, 5563231, 3653911, 2713981, 7607317,
		4985261, 4077443, 9670733, 3629909, 3341903, 9242231, 6691423, 9663217, 5691493, 6293387, 9065891, 921409, 6622907, 2274703, 5237923, 5314073, 2540969, 6994007, 6865759, 312089, 9791533,
		2586187, 8365883, 429161, 8419571, 5890303, 6219853, 5013779, 4383439, 4591091, 3258487, 5848783, 984167, 787747, 2619647, 739693, 5488823, 3505543, 6000937, 5185417, 858749, 4497011, 9824029,
		833759, 2142601, 7102759, 7731337, 2768209, 8784547, 4456547, 2730683, 9209731, 2149687, 2800843, 6239369, 742991, 4350803, 8285293, 5991653, 9926743, 3432031, 7317929, 7325509, 453377,
		6216929, 8063509, 8271317, 7055761, 4419293, 2717411, 818473, 3735553, 3619549, 3878713, 9567017, 4980347, 4009679, 2983873, 585619, 9298889, 7099843, 6470083, 2923787, 6604049, 5021729,
		2126429, 3100501, 8979259, 4931639, 4175869, 3786493, 7127363, 8914937, 5166023, 5574187, 2194229, 7898669, 4426781, 3147953, 5941399, 5723999, 6057521, 230861, 4822187, 354743, 4401919,
		3889331, 354839, 5634799, 3212021, 9666661, 4664419, 4539331, 6193711, 9850889, 2712379, 9489367, 7794307, 3614813, 3813521, 8574323, 8236073, 3040579, 9848273, 872561, 447233, 4450507,
		7607297, 8204597, 9129569, 9460897, 6873583, 5358169, 263881, 8603293, 2409863, 3008513, 9671813, 7961929, 9049211, 3829141, 965101, 3142939, 2119913, 6280331, 274019, 3135389, 2215097,
		7910989, 8418331, 3644873, 9392729, 3445493, 5224451, 8210183, 803483, 232937, 2403083, 2712883, 2377693, 8574089, 4807679, 7200821, 3426119, 2835367, 7943911, 952883, 5199109, 7392373,
		2498173, 4423357, 5528707, 635347, 5213059, 762401, 7579867, 9449131, 8095181, 4049351, 7781819, 2741581, 520409, 4170277, 5663521, 4465819, 6644887, 9876019, 3169951, 2934881, 5979067,
		2014127, 2717339, 6952403, 4261651, 6373973, 9711511, 4419469, 313477, 6835007, 4037479, 9272353, 3666613, 3457019, 6022073, 9172577, 5197403, 7820063, 6973259, 2032783, 583909, 3750547,
		5430079, 9293393, 9336263, 6586039, 4138369, 3191219, 7942523, 2280497, 8946293, 280129, 6504257, 2121011, 9912659, 2004901, 4903453, 3361829, 3070129, 7657933, 3565409, 168211, 449459,
		8626517, 6827839, 8475769, 5806729, 8772523, 6012731, 7972817, 4922717, 2818537, 2037131, 7484363, 6908183, 9747337, 4196713, 2685509, 3872527, 3161321, 9113719, 2283719, 8866079, 3847421,
		5004221, 2652791, 5464273, 2646001, 6806671, 7298209, 6918481, 4214207, 3401327, 964219, 6459659, 4251623, 113147, 915223, 6058621, 251353, 5292451, 3124717, 6270601, 3763211, 3889399,
		6526159, 7907489, 7128617, 3182341, 8144237, 3094361, 95531, 4309757, 5845877, 7271027, 4983217, 4595863, 9384703, 5664653, 5644627, 2036171, 8648531, 6785761, 3737659, 5617879, 6014521,
		590987, 2104703, 9827347, 4496083, 6592661, 6865363, 6689873, 2741593, 456349, 4194823, 5647591, 5765933, 8001589, 5338451, 6497033, 9301739, 7876471, 8635477, 9176009, 3339403, 8681587,
		4302901, 3747539, 9774467, 2882483, 790567, 3632411, 3764447, 3792977, 7574467, 6315973, 3737183, 6797447, 5261341, 2699129, 4340057, 667363, 4709759, 2040377, 5218951, 2987849, 4144201,
		2984207, 8931947, 6163889, 9443839, 2065379, 5248003, 3023477, 5176709, 503213, 747493, 6136321, 5087293, 4820807, 6775441, 9614159, 2231717, 8079551, 704521, 2010733, 3273601, 7719419,
		3259489, 5915683, 5570377, 5224447, 3273533, 238841, 3169541, 3003499, 4767179, 7977577, 7592513, 4433881, 5742683, 4875763, 4377229, 8863651, 4743007, 2588107, 8696267, 505657, 2815507,
		380777, 207463, 2651629, 3235207, 3909277, 7897271, 4344667, 2127557, 5806117, 5452933, 7382951, 593689, 3816013, 5443993, 6708607, 2441807, 4605199, 9284281, 2001787, 7018069, 5800987,
		5866709, 5065583, 5557031, 5140777, 2974561, 9896459, 9516361, 8178857, 5404313, 4272841, 5853277, 2640779, 9478811, 270659, 8229077, 2655071, 6309731, 9485057, 9838133, 5426177, 556037,
		9733001, 9073919, 2536763, 4102409, 3290797, 6943619, 8230223, 7193377, 6017489, 9939539, 2249941, 6277171, 301027, 8401457, 9261619, 4526363, 4011979, 7610231, 6237599, 6201103, 9915887,
		8950027, 371353, 943781, 9354893, 893449, 3188543, 7318351, 4520431, 9939821, 9428323, 386233, 968041, 9268957, 6371083, 5082589, 8208763, 8230967, 9785827, 8299663, 8387473, 8801033, 7335179,
		3527281, 4950599, 8395973, 3968551, 5139203, 9592567, 9153509, 7364977, 774343, 6774541, 9295019, 3496343, 2917939, 4644691, 854729, 5959087, 7309223, 7594049, 538411, 6186497, 9698333,
		2997919, 4034761, 7411021, 5856797, 4894999, 3481999, 9342041, 8692127, 2616221, 4228429, 8577259, 6454873, 825281, 9265967, 3511327, 7954931, 9603103, 4807091, 3528773, 2982173, 7931881,
		2947471, 2730899, 4228489, 6622069, 4596281, 7203437, 4155467, 4858093, 3014273, 8784221, 2552317, 7523977, 7149049, 7070003, 7747979, 7537729, 205651, 4066067, 5227283, 2438417, 5040439,
		9753323, 4081013, 9459421, 4064171, 8304629, 3451111, 8640017, 7956379, 4900747, 4930381, 2659127, 2359783, 3432761, 4272449, 4301371, 3565669, 786673, 4340891, 3053231, 8979721, 4027747,
		7654271, 3324697, 6700957, 3162947, 2483599, 2617633, 4255817, 3251909, 7810321, 2489441, 2379203, 7326731, 5121227, 5956967, 2514263, 2169331, 4297763, 4530469, 6477049, 700573, 4875163,
		8302499, 64877, 3950179, 4691909, 5486911, 6102589, 3243397, 5550439, 7686053, 7543351, 7952677, 6147389, 8150977, 5585443, 3637897, 4475413, 5572477, 4489663, 8011979, 6981251, 3655019,
		2724829, 7620871, 7186759, 7348043, 860663, 4556917, 6761159, 8981299, 9457897, 2471347, 7354609, 71807, 9995101, 2225683, 7468229, 3876641, 3051101, 8515597, 8749337, 9870173, 5192029,
		3109751, 4470467, 6252557, 6223747, 3322261, 4883071, 9452491, 5848159, 8625121, 9412873, 7689607, 2679433, 4612177, 4867301, 7969219, 3314039, 8454023, 2106239, 4468579, 5408437, 282157,
		9342007, 9435331, 8464591, 7264003, 4991551, 3100949, 5185009, 6957179, 8877229, 9301991, 6031327, 481997, 3427537, 8487313, 3628193, 8913187, 3221017, 7481381, 6820493, 8905543, 6776113,
		9674681, 7697857, 6177707, 9011881, 165463, 3191897, 5298077, 316177, 9231293, 4932727, 729457, 5287307, 7866259, 5617349, 5570533, 4693669, 7372487, 881071, 2294321, 4988891, 2147213, 135731,
		748597, 4720907, 6816829, 9989927, 3476299, 7923001, 5210203, 2122459, 2802131, 6795647, 7589489, 3758537, 5819987, 2771933, 5561821, 5028827, 980689, 6310517, 9949943, 360193, 879581,
		9311173, 56197, 5741609, 5735959, 4901333, 2075833, 5929291, 7782629, 2745913, 5293861, 7000429, 537899, 6436571, 9173357, 3810349, 9308267, 8487103, 7395781, 3425791, 2198473, 6308461,
		9398419, 4936879, 6240137, 9454481, 8927069, 9742121, 6817501, 7299113, 9308161, 5810381, 8077789, 4179839, 3046709, 8674891, 6578639, 5875847, 8019353, 806761, 8968943, 597361, 3950939,
		9954601, 2821627, 4665653, 201499, 9188401, 8775373, 8491591, 9288151, 7772461, 4253423, 6150317, 3501613, 3744017, 4519973, 3199127, 3518147, 210599, 2996681, 7191181, 796171, 7577243,
		7227881, 468049, 9569773, 3711199, 6415733, 452873, 2430877, 546047, 9559477, 7638269, 281959, 6807923, 4372639, 712531, 9334613, 2690411, 7323479, 2189783, 3110227, 90901, 2533199, 3534799,
		4233301, 4695799, 7448509, 229507, 149011, 5781847, 9113891, 641441, 3294503, 7348637, 4925573, 6687853, 4765027, 8799463, 3022801, 4120553, 5284001, 3042367, 8500181, 6618229, 9687989,
		6363139, 4306801, 3078367, 5586983, 3863687, 2497867, 9815737, 4406411, 5390467, 4097209, 7868759, 8437543, 3738023, 632923, 7942799, 5868103, 8549531, 59957, 7081553, 7606901, 294659,
		5068829, 2189323, 4654439, 2490119, 9887767, 8843903, 8068373, 7148809, 5596223, 3536977, 6605693, 7713659, 2203933, 7174711, 848599, 752009, 289141, 580381, 9770261, 3867257, 6638231,
		5573507, 3743081, 7745077, 5939057, 9486431, 3128129, 2634761, 9953299, 3524453, 4390621, 3883379, 4710473, 560783, 2582761, 8240033, 7201457, 599519, 7651867, 6972359, 720847, 7519987,
		3521933, 988217, 2368393, 2609489, 9765187, 370561, 6581161, 3366659, 72461, 3610391, 193373, 3390991, 3464579, 2145821, 8534341, 2450249, 3612437, 7860163, 8821633, 5565269, 4011061, 169831,
		8637637, 4108063, 2080289, 5422463, 6652799, 5888657, 191861, 9037703, 8008549, 4817119, 5621939, 2660743, 3154153, 2006869, 2867377, 8098861, 7272179, 4923209, 6342647, 6897607, 821069,
		9418067, 2046119, 8976659, 9127523, 5183221, 3465611, 8891891, 6097439, 4748903, 9801557, 3223769, 552481, 5631221, 6658903, 2598521, 5185667, 8250443, 3471917, 2766521, 8739823, 3862013,
		9779069, 4050133, 3099419, 2565679, 3849821, 5773451, 4023881, 2563921, 6992429, 7513603, 2639083, 3689209, 202823, 6705773, 7138363, 657973, 223507, 8488339, 8875463, 9053909, 356977,
		5487091, 7357417, 4094837, 8895049, 9035951, 6965701, 2764609, 711967, 9942659, 8166007, 9624191, 2295341, 4437379, 5488429, 5135653, 7545247, 2159701, 2714449, 6287783, 3360569, 6051007,
		7761889, 5465459, 7660001, 7797043, 2125327, 2384821, 8492489, 5691947, 9539627, 3236713, 122029, 9412343, 8945879, 4572181, 987097, 6281459, 2644399, 8209109, 4387441, 849943, 3913067,
		3935627, 901169, 412481, 181669, 6729647, 4143409, 5006527, 2666539, 5992379, 5822521, 7337503, 8980997, 5521181, 9086881, 7267153, 3482201, 3858499, 9573913, 3597589, 3061117, 2229569,
		7374569, 9032039, 5310593, 6543599, 5273201, 3917093, 2490473, 3059291, 7159307, 7076221, 4360273, 7804453, 384113, 8917043, 731447, 4668071, 9507383, 5984257, 4145993, 7838119, 5977409,
		2179649, 4118339, 7183277, 8062303, 3661573, 3691859, 3452549, 6265157, 5398919, 2554429, 2187919, 6223471, 953179, 4842911, 282661, 5389441, 5847889, 6994919, 4896949, 981601, 9104659,
		6161009, 624721, 5201401, 7127567, 6466037, 9006671, 7282463, 8791801, 7227877, 863897, 675823, 182129, 851843, 135511, 2613139, 7166989, 6774149, 4697599, 4360001, 325439, 8458067, 6623423,
		6330041, 2876983, 3007001, 2412653, 7048771, 5716157, 69997, 6660977, 2026447, 2206669, 441517, 3362657, 3806981, 828557, 4660009, 4586327, 2213833, 3123583, 5428921, 5097247, 597473, 995833,
		8685629, 8573239, 7189339, 93481, 2792843, 3550163, 2894303, 5012177, 714673, 2263687, 5361779, 8995247, 8191123, 8001029, 2197501, 2839621, 9959869, 7513327, 3616661, 7035869, 6197209,
		4689871, 7242241, 7030477, 3830557, 5003077, 8207027, 5655571, 7872559, 4371151, 9035809, 9659653, 2724727, 565387, 354091, 2552587, 9285193, 560207, 8644331, 7623779, 9591649, 559703,
		5266907, 9074441, 2027759, 2927261, 6113209, 2155259, 7494743, 4499683, 4501949, 5348059, 506423, 5348263, 3948179, 7370609, 273899, 3883211, 5524699, 4849529, 7698349, 2380951, 992863,
		3138067, 5719499, 7061701, 300151, 9645133, 520381, 3452459, 3356981, 486103, 3674597, 5982173, 3028973, 9873299, 5409713, 6316969, 4643213, 2388361, 8267101, 4311701, 6615251, 6895787,
		3709499, 5788967, 8013377, 2349343, 5935109, 7718299, 651191, 2653789, 424001, 4731289, 4239731, 2907869, 8338271, 3702529, 3655987, 4951787, 429727, 305113, 5165803, 4034467, 8263823, 74441,
		174989, 6076571, 7325413, 9322109, 517211, 7495511, 9388697, 4500577, 2120423, 7769819, 4665979, 2385989, 6536363, 7098331, 4414999, 7167373, 5840701, 4708381, 4229941, 6222883, 8732861,
		2545177, 8950489, 2923163, 132299, 8565509, 5630591, 3784657, 4057349, 9613127, 2329667, 798079, 5458487, 2873447, 3045109, 7685833, 7860107, 4608953, 5130571, 7042193, 9861349, 9399191,
		8458759, 4746377, 8385877, 5959403, 233071, 2601367, 6879673, 5727919, 6101167, 4202633, 4460977, 9296561, 3056453, 4349621, 9682339, 7222763, 3499711, 546863, 3890771, 8417911, 5420539,
		2723363, 4896061, 5717837, 8316967, 7870183, 6574559, 3453379, 2692759, 8759507, 4294357, 7968487, 7889723, 5375443, 3139583, 6625669, 8455243, 8428789, 4967293, 8366903, 5671111, 2122277,
		582809, 3080503, 4781983, 921223, 2735713, 8937931, 2991083, 7834721, 9163243, 4281493, 9599071, 7375481, 2288107, 6356443, 2184863, 5363461, 7403633, 8342183, 9884543, 2047439, 5534341,
		5219441, 2564101, 3864703, 769837, 8599273, 8364443, 4508617, 7009777, 7770337, 5216083, 2124173, 5980747, 9248081, 8192143, 2575901, 2972537, 3696863, 9303677, 7076903, 4667629, 9413879,
		462067, 255071, 5490451, 7195217, 5922877, 334021, 4753711, 4520237, 3508357, 4488179, 3922691, 5128363, 5200061, 7872353, 8611723, 5509429, 9581813, 9742643, 6973609, 3248657, 6909613,
		9339227, 3514349, 8565281, 4177339, 6933947, 9968417, 7678397, 923711, 5108963, 7894643, 6495661, 5007529, 2172227, 4188997, 4984687, 737969, 5901109, 4573883, 754133, 6872273, 6767491,
		2732243, 566173, 3808267, 3135163, 9825589, 6031411, 9246847, 577867, 6212147, 5783693, 4930729, 5284619, 3603883, 3627181, 5233979, 8373847, 2314589, 8645971, 2384143, 665069, 509147,
		4275617, 6210749, 2743541, 6428819, 7570103, 4093181, 3970579, 7280113, 8902417, 808373, 8000051, 6416759, 5170519, 2936039, 2977817, 2441129, 2105891, 2472611, 9554837, 8607227, 6517003,
		9824207, 774467, 851239, 6182531, 9622001, 8392453, 8187373, 9323557, 166247, 8750689, 5051219, 5203249, 2493949, 5159611, 167191, 5615411, 7239187, 3813233, 871231, 6740087, 790991, 8758637,
		8933159, 181061, 7859419, 210961, 7786787, 4740649, 2319181, 9117187, 8104799, 6242363, 2325689, 8573051, 5051113, 3609799, 3819583, 7348007, 6209353, 5952689, 6411883, 9805001, 4743041,
		3514013, 2433307, 3130453, 3493703, 3020827, 2002883, 952927, 9799579, 2031611, 7249441, 3116719, 3168959, 5547599, 9301973, 765619, 2786909, 7609243, 8574229, 4789601, 9108241, 8940037,
		9084503, 9317237, 6611141, 9279769, 5381689, 7828913, 6424771, 2118923, 5978117, 3358099, 783487, 8935889, 858269, 3917731, 5148761, 602111, 6088403, 5869159, 4078817, 7567319, 9293551,
		830293, 2223773, 7800733, 4993579, 5055203, 8828663, 931289, 4197097, 4083199, 5562793, 7872653, 5749157, 5744659, 199679, 7625377, 4585561, 479431, 4940651, 7385687, 835039, 9127187, 4770079,
		8378299, 4272553, 7414643, 3335819, 7228129, 3649579, 9843553, 3848447, 4615337, 8820013, 6047267, 5803471, 9907841, 5748079, 9781699, 8014001, 5321647, 5742509, 6775471, 9450941, 3927713,
		679463, 7385591, 4367749, 5823967, 7744259, 383767, 5043457, 7887083, 815471, 930689, 7051411, 3818369, 4042091, 7386073, 8540867, 3487021, 8793481, 8945969, 4514597, 617269, 5962157, 4353091,
		8774869, 3539897, 3290431, 3485617, 5329141, 2997721, 8553569, 3061427, 5161349, 2689861, 8633941, 2763947, 8659921, 7298077, 6737449, 8695009, 615827, 9991811, 2592523, 3890083, 2082569,
		739103, 9316201, 3176111, 5154973, 3953303, 2754127, 2691853, 482707, 2694457, 8835523, 7860989, 2823833, 5233727, 7482221, 9569143, 4336847, 5052283, 3023633, 6545053, 736367, 8991431,
		8008093, 245339, 8007691, 4426337, 6505181, 3000047, 9657821, 3584323, 7945277, 3319969, 3288713, 2853451, 2007613, 234083, 805081, 7084859, 908053, 3674617, 2640349, 4761971, 9040399,
		5514349, 9636299, 6736297, 4396787, 2982137, 8696867, 6980327, 200351, 2484919, 8395349, 5158117, 2007899, 4019413, 2972821, 3824827, 4411573, 794711, 4514567, 357689, 3948493, 849931,
		3007183, 3999733, 2968573, 5871599, 526387, 5113403, 6718997, 640411, 730339, 649969, 8732641, 6746471, 7702559, 3479129, 6218449, 8134727, 6491533, 3401777, 2824793, 6822971, 3478327,
		7592261, 3452929, 3852683, 5003267, 2605501, 9396701, 5565643, 2291909, 5291249, 8586533, 9354503, 6023659, 6461839, 4098709, 831911, 285871, 6651077, 2335441, 3005911, 113497, 2328047,
		393361, 5650303, 2982011, 3649609, 8621677, 7677581, 3138449, 9337219, 4191179, 5303737, 9388517, 9325499, 6652441, 6769423, 9504533, 8067511, 5658397, 3190637, 8232083, 439723, 6401893,
		7675763, 5551879, 7178261, 5240819, 6717551, 2618191, 3999791, 6811789, 8913521, 8235593, 5945851, 3688241, 7267907, 6428671, 4919977, 2008247, 4257523, 2046017, 5579263, 290663, 2340277,
		8991799, 283937, 6795469, 6787367, 6503407, 210869, 3258763, 2548891, 4820617, 5579033, 633187, 692161, 7169653, 4845539, 2993447, 5244011, 322781, 9512803, 8172469, 6196961, 2753279, 259429,
		5044031, 4455833, 482743, 4115117, 7246891, 4324519, 101603, 7577639, 6246061, 6456851, 7957501, 6954083, 8933489, 2868673, 3200767, 9830671, 3948541, 450019, 3415343, 5371819, 9179011,
		4091657, 4359353, 9333067, 7526633, 7054057, 2355439, 7486373, 9389641, 7589821, 6000149, 5871343, 7057951, 3963649, 6617489, 6232043, 4899143, 8118359, 393667, 9716969, 992231, 7629989,
		8445607, 6814627, 549623, 2530267, 5040913, 3693617, 2676241, 702313, 457789, 990463, 8218229, 8915393, 6369971, 397753, 7368091, 7346167, 7199029, 9653443, 7820479, 766487, 7726063, 6754393,
		4041899, 9063623, 7742641, 7488259, 4699571, 4507637, 914873, 7647659, 8668549, 9602731, 5292607, 90031, 8632453, 5701517, 4963421, 2815469, 8863147, 4021379, 3487709, 3916813, 7132063,
		616717, 2393929, 8227781, 9613327, 4246523, 2538959, 2181217, 2147473, 3136841, 9696091, 9402539, 7171201, 5826911, 9838097, 3894571, 3537979, 650537, 7722851, 5806259, 2609699, 3160621,
		4644631, 5254429, 110039, 7627733, 5335273, 2703011, 2201489, 5162231, 2787419, 9066803, 9479647, 4943797, 3245087, 3746219, 2490451, 8515721, 6857759, 7040543, 6671683, 9519319, 7592539,
		2906231, 7538827, 5567297, 7158461, 504379, 6843451, 271969, 941669, 8229857, 3944231, 9342199, 6008207, 480143, 3269213, 2347339, 687683, 8177747, 7822109, 4915223, 7452961, 2783611, 8616149,
		4847989, 9130553, 2289037, 7261763, 338411, 6553387, 4617671, 4428869, 2181449, 9588107, 9501529, 3839839, 5930809, 9045851, 4979831, 2222567, 9740869, 5724799, 4158313, 2845207, 5244647,
		7135889, 8138993, 2582077, 468509, 4991867, 786251, 4234247, 8791751, 6855379, 4423703, 612809, 4519057, 8858293, 5428691, 9387071, 9241669, 8135927, 865721, 970747, 3205981, 4383833, 96737,
		2870047, 3556897, 8097347, 3302149, 2397463, 816769, 2340563, 3337883, 6008131, 870407, 6309629, 7502129, 7305791, 3621263, 959869, 8139959, 5066011, 706313, 8861513, 7082011, 8708803,
		8611597, 7462727, 5002457, 9117209, 5666887, 3818231, 9532063, 8949077, 8222609, 3158537, 6668119, 2899717, 5079341, 2825489, 9529519, 6985931, 6105809, 9332887, 5960501, 6857621, 4851541,
		2573653, 3031121, 3352511, 7390219, 8028439, 3577727, 2981779, 7620839, 3108319, 108709, 8164069, 6364763, 4687873, 8364929, 7147859, 9881713, 2899021, 6299459, 8218669, 3572321, 2355043,
		4802153, 2002361, 4444109, 4096273, 4605061, 9946441, 885623, 9856139, 7932697, 9503297, 8427299, 8802943, 7795441, 596363, 2994361, 4394029, 2759411, 2289751, 4965613, 7150459, 3629921,
		3747587, 8530811, 6277639, 5499107, 5046281, 817567, 146933, 2146051, 9825391, 2393653, 3479699, 6442049, 9262079, 3764899, 4900999, 8907557, 2323423, 9067393, 7412059, 8108713, 8378989,
		814771, 4226219, 5600717, 403309, 2174299, 2759513, 2458303, 3645713, 8866157, 5512049, 7838587, 6018829, 5807413, 925469, 4421491, 4016167, 4003213, 7139663, 9588899, 5165887, 7056167,
		7757599, 2996377, 3071093, 2433433, 8407433, 99223, 8327443, 6375403, 9142333, 653117, 2703461, 6175243, 6586319, 4771999, 8997943, 6778027, 6052873, 5636971, 5658259, 820177, 2586497,
		5586611, 3068869, 5007703, 3709357, 9417799, 3815407, 6653219, 789709, 8527499, 5231491, 3354283, 6471643, 4489319, 4361711, 2561233, 5248907, 4849007, 9296191, 5653201, 3367027, 8672177,
		9745663, 3312539, 6238723, 8065067, 4496057, 9777301, 4181351, 6524537, 2866583, 6027661, 8615099, 910807, 8401513, 367229, 8590759, 2020247, 6108013, 87517, 2292239, 9812447, 3450467,
		5613889, 7116299, 8163983, 2805659, 9200327, 2747923, 8761733, 9703541, 2649293, 7120837, 8878477, 139133, 544879, 9036827, 7443911, 6896893, 7947613, 2828809, 6481619, 8456233, 4352113,
		7012697, 6343847, 6926693, 488993, 2527403, 3125819, 3774671, 9962957, 6446221, 2453201, 5122477, 7565567, 2932679, 5588783, 318337, 814873, 2722399, 7722271, 8358953, 3581251, 8922703,
		3757729, 6061309, 2412077, 413477, 3762601, 8692237, 6423803, 7257157, 7126727, 7084261, 5414273, 3365777, 5721901, 714463, 7793999, 9805891, 5424301, 895669, 6571421, 6311413, 8172389,
		4136861, 7311449, 9729613, 131363, 2701871, 5588663, 7507751, 4415899, 9468601, 3262681, 7975651, 666959, 3713027, 6946463, 4177997, 4860311, 582221, 934907, 4978829, 3373243, 8928511,
		6054341, 704849, 7885123, 2982823, 2306627, 5495291, 7103077, 603173, 5465969, 3586559, 4690123, 423587, 3819757, 86453, 6880507, 5358217, 4434539, 9420223, 9958759, 187111, 4766189, 3166453,
		7213841, 8395027, 9952337, 9848857, 3932633, 3641593, 4581581, 952957, 340387, 3197923, 5214467, 7705993, 7405679, 2689667, 4074601, 4514443, 3402071, 5563501, 2433931, 376757, 9247969,
		8547509, 402949, 5283961, 5334103, 9604697, 290959, 8875739, 6899077, 3464407, 8186599, 4646233, 952997, 149027, 5748881, 959681, 7144001, 7415869, 3167561, 5068439, 5050691, 3112787, 2462843,
		5331379, 2098781, 4349489, 9646141, 7618243, 5949313, 6579883, 5872681, 2598181, 5441123, 639713, 7326101, 6979103, 3425351, 3046721, 4358969, 9029131, 640247, 9430103, 471943, 4179463,
		874721, 239977, 6977587, 4938553, 8834537, 5054167, 5775409, 9728009, 2420981, 3443369, 5473109, 4039433, 7674047, 9115633, 104743, 8346521, 7928903, 6238787, 9101867, 6464057, 574393,
		6328027, 9099217, 5483957, 2377117, 8976757, 3337421, 3413929, 182969, 496477, 7427051, 9985267, 6226313, 6986731, 6558899, 2361071, 3864629, 2877839, 5120273, 6369667, 3665633, 9288823,
		4097321, 7710343, 6572029, 2215133, 7207243, 5263883, 968197, 6557179, 5728363, 7959493, 6736019, 9000217, 3346219, 5613787, 9219107, 9715639, 6995101, 530597, 4168133, 3583133, 7933867,
		3101311, 4554163, 7182121, 8105563, 7190971, 5336269, 8352133, 3569003, 9496771, 2221997, 8112851, 7161071, 2040149, 3066157, 3166253, 2420519, 7531637, 8432509, 802511, 942763, 5968133,
		5731981, 6609571, 9969793, 9170423, 4921673, 4263697, 9695333, 2705341, 77573, 4747033, 9923789, 8668721, 5266507, 6120007, 3730579, 4273831, 4647263, 2932703, 2152433, 3978179, 4223699,
		5577643, 7507789, 9517111, 2891891, 8762063, 5800369, 3477541, 4880437, 2071259, 8911907, 562333, 908591, 8372417, 4732109, 2990753, 997379, 871349, 4268219, 6358073, 8537801, 5171123,
		6839873, 4619399, 9104209, 2330161, 5586121, 3669763, 2578847, 4127231, 4405501, 8369773, 3690613, 825749, 8104639, 9673043, 9284449, 4772623, 9349421, 57689, 7078823, 7925867, 3397873,
		3663911, 9928367, 6453511, 669413, 7382581, 5651309, 3704507, 6520883, 7835609, 7235203, 9081467, 8255869, 501803, 5635499, 111217, 9934471, 2694607, 6333419, 7255639, 6809767, 8732117,
		2412833, 2326937, 3531299, 5911127, 3214703, 360589, 9778403, 9230773, 396953, 6640849, 3215713, 3345841, 9068161, 2352107, 4145567, 3778391, 4777523, 8724101, 8387857, 6274673, 870301,
		2369441, 5481913, 7027723, 2316493, 8287273, 7680559, 7104341, 3125587, 3741833, 3333217, 5667421, 2777909, 4953233, 5955539, 4664789, 7472627, 4798361, 4904269, 7110511, 3879739, 4800557,
		5767637, 2312983, 5573147, 8666017, 2425723, 6299591, 4218439, 7525013, 9530467, 2010061, 2209547, 5815231, 9827353, 717151, 7013381, 474629, 2936327, 4222871, 6516931, 3326051, 9936617,
		6443959, 458323, 3412813, 6248969, 5167817, 3968893, 6221561, 3363643, 2026613, 5804137, 4003513, 3871081, 2958049, 4299329, 7266031, 4599281, 9303703, 522757, 8978353, 6084293, 3645149,
		7506253, 7882219, 2648213, 2283499, 6895771, 7992701, 5122639, 5675933, 6231223, 8246101, 2456459, 2759087, 415879, 2902661, 9743179, 920209, 2727223, 4389439, 2344717, 3971053, 7296193,
		8183873, 5410567, 3509917, 3293429, 3853567, 9711787, 7588241, 8864371, 3974681, 6531527, 5368471, 8184907, 7552453, 816329, 9117883, 824531, 3699301, 244843, 3707453, 6184811, 3122719,
		2681863, 2447581, 4192361, 9525227, 2811241, 2937611, 9393557, 687299, 3613199, 9683123, 2624107, 875447, 325219, 3269809, 4975319, 8886811, 7028389, 6197881, 68147, 334637, 5370671, 2251369,
		8863973, 3790487, 3578717, 6703597, 6149669, 9250139, 2584529, 8147549, 8073451, 115741, 2626837, 4758749, 4070167, 265493, 3238801, 9491969, 5188411, 545873, 2874791, 2781887, 8418359,
		3911779, 6477343, 4300789, 7305511, 2063021, 7520719, 2352611, 4330583, 2222593, 5361751, 622241, 574643, 3314203, 6737009, 5672633, 8683793, 3885727, 8912699, 9637951, 8763637, 9184309,
		2853509, 3330013, 510271, 6884357, 5076941, 6473981, 6352051, 4998671, 2565049, 4124207, 5921809, 2080651, 751007, 8795483, 76207, 7193149, 7923857, 9269971, 6951121, 3790121, 9674669,
		5062459, 3926917, 2845811, 5565821, 5550821, 9985421, 8348443, 6683867, 2880859, 3538217, 2764667, 7396007, 6329369, 8908483, 3737647, 6174331, 5794037, 6953131, 7668839, 3820171, 3001577,
		2117233, 9300437, 2472893, 5289121, 705833, 2199859, 8237461, 2819347, 9355141, 4109011, 7100221, 3263441, 4038761, 7043173, 6636173, 270961, 4583471, 5717743, 5457701, 129001, 697643,
		2064929, 3653791, 449203, 6280237, 8278577, 200461, 8947751, 6270553, 587563, 7032059, 9682853, 8746117, 6989537, 5642599, 8366609, 463891, 6731759, 8055779, 5724941, 3393629, 8880457,
		4975331, 4341487, 715087, 2986037, 189613, 4047529, 2570801, 7293059, 3637343, 2015779, 8810341, 8350259, 8573249, 7883717, 3707773, 6895003, 7324579, 732133, 9806969, 4312003, 5088341,
		474547, 4436297, 2500087, 5978617, 8489183, 907111, 4682779, 4742887, 2499859, 3070061, 6487067, 4504979, 6077639, 4524229, 4429631, 5865983, 5226367, 9691111, 2117833, 3492457, 8003069,
		2362273, 6426367, 7190851, 7306807, 2421733, 8472881, 7844831, 9598811, 6849971, 9817447, 6100049, 6332927, 8601647, 4023053, 5581963, 3139327, 3089857, 4477661, 2387533, 5295863, 9985207,
		2526547, 9372953, 7487341, 8560831, 3721477, 4833109, 2522329, 8166359, 8243633, 249427, 3181861, 9197381, 400009, 3762613, 9845677, 4196659, 8280373, 7135691, 6898051, 4233989, 3725693,
		3507461, 9195311, 6280853, 3167581, 6087937, 7578721, 5030981, 656891, 3850199, 7872443, 3178849, 2074207, 861799, 3338249, 6933257, 6659663, 2626643, 9637357, 8027147, 4886963, 6436351,
		6460241, 2812751, 2523173, 915301, 5053891, 4458281, 6191593, 2962907, 9998797, 3756241, 8347253, 2343833, 4250809, 4719647, 4166233, 363037, 7417853, 5321651, 9131651, 5192323, 9594617,
		6934591, 8250037, 8456527, 6754493, 4485797, 4933811, 8907293, 5258807, 5843843, 9977447, 2865259, 4977197, 133097, 6072707, 5797213, 3734557, 6862333, 3236423, 248057, 9827297, 5574787,
		5011973, 574423, 7419029, 81901, 6407449, 2192423, 2693731, 8422549, 7891951, 4875179, 4316231, 5361709, 8630231, 8738273, 2387897, 5688511, 4138241, 5000399, 2593079, 4685509, 6396491,
		4684879, 9907423, 546937, 7125337, 4314293, 8984903, 7190119, 4348759, 485207, 734329, 7231369, 4366981, 3557551, 904997, 4618853, 3293639, 7653671, 5409409, 2123783, 3809677, 5031601,
		4042069, 8555831, 9511027, 3696593, 8806327, 4734031, 8830823, 3235499, 3412127, 5638021, 595549, 7743403, 5255953, 2484509, 252727, 4684297, 4077919, 3395831, 5445911, 3110339, 444469,
		5274109, 7644037, 5739803, 9450173, 5699357, 9836993, 2901527, 6360853, 2722789, 5702063, 2197753, 8713429, 7158013, 7925557, 6925619, 3144893, 2065579, 7942447, 3620489, 7411207, 4473731,
		9441197, 8773607, 2284493, 4651873, 9035657, 3671221, 7432981, 7624949, 275207, 5193677, 9635441, 4597787, 4256117, 2766779, 9685889, 4098217, 9216527, 3444827, 5975161, 5550029, 3595441,
		2642513, 7539017, 8056603, 640249, 4723877, 4658963, 9648739, 3325831, 2268337, 3295337, 8230949, 3724489, 2133811, 384737, 2550871, 658817, 9609031, 2902483, 4227973, 9090871, 9270377,
		8843389, 3995347, 6095927, 2240977, 9168407, 4595089, 2855911, 62687, 5305771, 8467183, 668939, 5483939, 4209643, 5258413, 4057663, 548069, 3193903, 974411, 9234223, 4014331, 2514377, 4834201,
		5311883, 5334761, 3056971, 2832953, 7051399, 8623687, 4655923, 8343989, 8020697, 5996477, 852031, 2304037, 4692011, 7012333, 7711789, 7027547, 6914353, 4808611, 90619, 3739793, 5643103,
		8767771, 6597119, 3620153, 9057949, 4806391, 581353, 5747179, 2657671, 5184373, 5506961, 2152691, 5115947, 562721, 3864929, 5730451, 6380281, 2923033, 6165581, 5356163, 4911233, 7064627,
		7839253, 2057299, 9703501, 4487389, 5987057, 2222239, 5144749, 4308251, 102259, 8980943, 6857203, 8801357, 6322397, 4327363, 6211631, 4527563, 391177, 4876447, 5871301, 9326861, 7906859,
		5322013, 8491753, 5306089, 7511891, 4915331, 3972643, 4518109, 8398393, 185897, 7302943, 6938993, 3022307, 9234749, 6291881, 2159231, 9514199, 6567557, 4697239, 6715573, 7804201, 6581921,
		2950291, 349663, 5191741, 355847, 4260649, 4879033, 9799087, 799489, 249583, 5284507, 8623033, 168029, 9583781, 7681507, 579949, 5827709, 3383959, 3981589, 6691843, 3530537, 6663557, 3070373,
		7436509, 2014967, 8573987, 2373611, 5115841, 279731, 6733151, 4831907, 4318157, 8795617, 6174001, 4734043, 4723661, 170899, 9080387, 403783, 4184603, 3555901, 182899, 617731, 7423331, 3171893,
		5696381, 6307517, 2139859, 6368129, 9056507, 9480083, 7743937, 4240277, 3942619, 7076761, 7515719, 9844889, 2307451, 9208151, 3256471, 3210719, 5487683, 2238319, 4727297, 2619821, 8449409,
		3258427, 636263, 3838111, 9614629, 9347669, 9579043, 348463, 3887399, 4720427, 2567321, 3503407, 655471, 8203777, 8903759, 5489273, 5771747, 200159, 9481471, 3977401, 2636251, 5557723,
		2545237, 3969937, 6213797, 5372137, 5460139, 7419163, 5715211, 3566249, 7895527, 8612347, 8767763, 3917737, 6196097, 8176823, 7094839, 2850433, 7854631, 3701507, 9299261, 6037939, 691399,
		5654017, 6712417, 3170467, 4433269, 9822887, 6441319, 4766117, 3365969, 549979, 4588321, 5899669, 4712467, 7789619, 7039559, 9360821, 4317487, 3060301, 4189099, 9486571, 5322517, 4423841,
		8555891, 5642999, 7388327, 5184457, 8773799, 9606539, 4025797, 7109351, 9273541, 7086367, 8933387, 9511289, 7536281, 9782503, 420569, 3799027, 4285339, 697211, 2199133, 6378181, 2435179,
		8432471, 7368301, 2325559, 4215457, 8515369, 6224171, 5698739, 5530087, 2389589, 8962013, 3147797, 7512709, 3494521, 8861249, 6066661, 893213, 880883, 8088109, 6927631, 9854989, 8053109,
		9936361, 3309847, 2600119, 719353, 4788241, 7766933, 92107, 2648827, 5299501, 2251559, 6388771, 8107961, 5047261, 9338453, 7281103, 7364017, 6930073, 7988473, 3565721, 2868043, 7631957,
		2413883, 8668571, 5039621, 5368327, 9588853, 6139319, 532639, 7147109, 791897, 8237543, 4425721, 9468247, 4231099, 4515493, 3495161, 9576577, 8206021, 7602433, 3268127, 4928747, 2652007,
		6846979, 8831587, 4849639, 5704991, 3644423, 9924601, 3821813, 3267041, 6383261, 4907537, 3764807, 4934491, 2657393, 6372271, 6663869, 6601211, 6862049, 5327339, 662029, 6515863, 962609,
		4648351, 2434423, 7474829, 4283767, 2047967, 2783017, 6358271, 6425267, 9095479, 481157, 2233039, 9121751, 8571701, 3106007, 3701723, 9382099, 9523697, 5110751, 6699941, 2314877, 7050859,
		6217859, 8511953, 2763209, 3390707, 6215623, 9889171, 4927823, 2111597, 289369, 5034347, 7322593, 617723, 5890117, 3501493, 3312209, 5361997, 4765339, 641579, 5085167, 2987519, 3448007,
		5100343, 4439087, 4937879, 5738861, 7071461, 2127163, 5084957, 5522897, 3000089, 3409577, 2115721, 2782279, 5640119, 6449693, 4448267, 2790421, 6131219, 2967389, 5558953, 6437737, 4516937,
		4391711, 817409, 9867037, 9913811, 777641, 3494417, 4337119, 8600941, 8226353, 757829, 9195359, 3422191, 2822987, 4674391, 318589, 9504097, 8359289, 7499539, 4686007, 8197543, 8388071,
		8692793, 5959763, 9439211, 8976043, 5368609, 8038141, 6459767, 4959151, 2768639, 6650099, 141221, 4955437, 7248643, 8719037, 2096009, 3973153, 4310989, 6169349, 9246949, 2930359, 8614369,
		9381283, 9773887, 114661, 2565349, 7949303, 9876851, 7200073, 3078137, 6881087, 9261533, 7138337, 2261269, 2852449, 9616657, 4382723, 2826091, 6802139, 8034997, 3205429, 8575201, 5646239,
		4589803, 7291643, 9088993, 7190279, 2468027, 885889, 3351419, 3128039, 3136607, 6788689, 142711, 5696659, 669479, 803027, 9970783, 8285983, 8038451, 5821177, 4374947, 2742407, 8562193,
		7554563, 2696209, 8075533, 6378241, 374443, 2583769, 8374841, 6020327, 5822111, 4517587, 5851117, 8585611, 2382859, 5420543, 3673777, 4150301, 3332863, 4518929, 4467443, 7199431, 8301983,
		6282137, 5803489, 9741691, 2380129, 4525811, 2750177, 320669, 5869781, 2526169, 2941291, 2373109, 5810087, 8972591, 6101873, 3252397, 7584463, 8832029, 8902969, 6116867, 3084163, 6521293,
		7952443, 3834473, 8651549, 6120997, 6080461, 672439, 5285789, 4674503, 8405077, 2381231, 7796003, 2905559, 5930759, 9364631, 603947, 5716649, 5065961, 3717313, 8749127, 95791, 8215001,
		2163079, 3537973, 6486959, 3916543, 642557, 7775399, 6939193, 4794239, 763957, 5047033, 7092109, 9159361, 4867147, 9628403, 5109431, 7527683, 3928381, 4345031, 2081039, 3039163, 4141681,
		6901399, 3880801, 2041027, 3503561, 8304973, 5140147, 9080821, 7474273, 5109719, 91081, 2063059, 4080287, 7006171, 9624889, 7482157, 5378203, 2146139, 886537, 5633059, 4929377, 6236687,
		3844187, 8619197, 2046371, 3141343, 6078797, 8548489, 4943219, 4764203, 246391, 9184661, 2414389, 7672349, 8914403, 4233961, 7296701, 7922531, 6334567, 8468639, 8025883, 2882653, 6352979,
		9007393, 9239423, 3313823, 3244441, 8715559, 6560093, 5359553, 8510059, 2529929, 4668539, 9608021, 7802237, 7624663, 5750117, 738349, 2672123, 7930849, 5843857, 9417229, 5721589, 9086647,
		3937399, 6485951, 4461277, 9151039, 608011, 814013, 2735191, 8702647, 3704537, 258949, 6577891, 9848191, 4674223, 4931699, 3156583, 4459177, 7842827, 82217, 6692813, 8535287, 3511493, 2785751,
		4194403, 7789181, 7951897, 2101277, 3058933, 3161891, 5039219, 9504689, 9636037, 7735283, 9165313, 9551159, 5658109, 5030689, 678541, 560653, 2720503, 5054789, 6783869, 5191867, 9810499,
		7328273, 517991, 8895071, 2106347, 5162627, 4577449, 7657723, 9237061, 4592131, 5560279, 8158013, 4294729, 957349, 9029129, 8757691, 5103071, 8236583, 816709, 4072531, 234383, 6020759,
		6444313, 350771, 2334047, 5824921, 5578421, 7414789, 576193, 4982707, 8496359, 3717563, 6164461, 794957, 5594747, 7333043, 4291673, 3818831, 2279087, 4646459, 200569, 9210749, 4872169,
		7291477, 5451889, 6151513, 8483467, 6240007, 9904291, 9544541, 7395821, 6686723, 6868061, 7858079, 6337453, 4228787, 4659197, 6596171, 6339647, 2431321, 5781857, 2979917, 4774667, 6796883,
		4315901, 8320393, 5201467, 110573, 6038441, 752581, 4794299, 6542551, 8659601, 7409551, 607931, 8968637, 2001347, 2107289, 4448837, 189139, 9878563, 4907473, 8522281, 609043, 812299, 4356307,
		4026353, 6203699, 4848659, 4623139, 693529, 3583661, 3344791, 8628149, 3190961, 3429431, 580301, 186917, 2406139, 2074519, 2116481, 2141801, 2698471, 8809001, 2483713, 6102407, 652997,
		7219273, 9676021, 7045081, 2120101, 6021467, 8362127, 2744587, 9085357, 514123, 6853031, 244301, 7266601 };

}

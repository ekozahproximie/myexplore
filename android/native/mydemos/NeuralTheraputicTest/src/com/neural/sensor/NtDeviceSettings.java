package com.neural.sensor;

public class NtDeviceSettings {
	
	// current values MUST map to Shimmer.SENSOR_XXX
	public static final int SENSOR_ACCELOROMETER = 0x80;
	public static final int SENSOR_GYROSCOPE = 0x40;
	public static final int SENSOR_MAGNETOMETER = 0x20;
	public static final int SENSOR_ECG = 0x10;
	public static final int SENSOR_EMG = 0x08;
	public static final int SENSOR_GSR = 0x04;
	public static final int SENSOR_EXP_BOARD_A7 = 0x02;
	public static final int SENSOR_EXP_BOARD_A0 = 0x01;
	public static final int SENSOR_STRAIN_GAUGE = 0x8000;
	public static final int SENSOR_HEART_RATE = 0x4000;
	public static final int SENSOR_TIMESTAMP = 0;
	//public static final double DEFAULT_SAMPLING_RATE = 512; // see Shimmer.java line 1492 to see why I think this is right
	public static final double DEFAULT_SAMPLING_RATE = 1024; // see Shimmer.java line 1492 to see why I think this is right
	public static final int DEFAULT_ACCELERATION_RANGE = 1;
	public static final int DEFAULT_GSR_RANGE = 4;
	public static final int DEFAULT_ENABLED_SENSORS = 0;
	public static final boolean DEFAULT_CONTINOUS_SYNC = false;
	
	private double samplingRate;
	private int accelRange;
	private int gsrRange;
	private int enabledSensors;
	boolean continousSync;
	
	NtDeviceSettings() {
		samplingRate = DEFAULT_SAMPLING_RATE;
		accelRange = DEFAULT_ACCELERATION_RANGE;
		gsrRange = DEFAULT_GSR_RANGE;
		enabledSensors = DEFAULT_ENABLED_SENSORS;
		continousSync = DEFAULT_CONTINOUS_SYNC;
	}
	
	public double getSamplingRate() {
		return samplingRate;
	}
	
	public void setSamplingRate(double samplingRate) {
		this.samplingRate = samplingRate;
	}
	
	public int getAccelRange() {
		return accelRange;
	}
	
	public void setAccelRange(int accelRange) {
		this.accelRange = accelRange;
	}
	
	public int getGsrRange() {
		return gsrRange;
	}
	
	public void setGsrRange(int gsrRange) {
		this.gsrRange = gsrRange;
	}
	
	public boolean isSensorEnabled(int sensor) {
		return ((enabledSensors & sensor) > 0);
	}
	
	public void disableSensor(int sensor) {
		enabledSensors &= ~sensor;
	}
	
	public void resetSensors() {
		enabledSensors = 0;
	}
	
	public void enableSensor(int sensor) {
		switch (sensor) {
		case SENSOR_ACCELOROMETER:
			enabledSensors |= SENSOR_GYROSCOPE;
			break;
		case SENSOR_GYROSCOPE:
		case SENSOR_MAGNETOMETER:
		case SENSOR_ECG:
		case SENSOR_EMG:
		case SENSOR_GSR:
		case SENSOR_STRAIN_GAUGE:
			enabledSensors &= ~(SENSOR_GYROSCOPE | SENSOR_MAGNETOMETER | SENSOR_ECG |
					SENSOR_EMG | SENSOR_GSR | SENSOR_STRAIN_GAUGE);
			enabledSensors |= sensor;
			break;
		case SENSOR_EXP_BOARD_A7:
		case SENSOR_EXP_BOARD_A0:
			disableSensor(SENSOR_HEART_RATE);
			enabledSensors |= sensor;
			break;
		case SENSOR_HEART_RATE:
			disableSensor(SENSOR_EXP_BOARD_A7);
			disableSensor(SENSOR_EXP_BOARD_A0);
			enabledSensors |= sensor;
			break;
		case SENSOR_TIMESTAMP:
		}
	}
	
	public int getEnabledSensors() {
		return enabledSensors;
	}
	
	public boolean isContinousSync() {
		return continousSync;
	}
	
	public void setContinousSync(boolean continousSync) {
		this.continousSync = continousSync;
	}
}

/* Sampling Rate:
 * 
Byte Value	Hz
1	1024
2	512
3	341.3
4	256
5	204.8
6	170.7
7	146.3
8	128
9	113.8
10	102.4
11	93.1
12	85.3
13	78.8
14	73.1
15	68.3
16	64
17	60.2
18	56.9
19	53.9
20	51.2
21	48.8
22	46.5
23	44.5
24	42.7
25	41
26	39.4
27	37.9
28	36.6
29	35.3
30	34.1
31	33
32	32
33	31
34	30.1
35	29.3
36	28.4
37	27.7
38	26.9
39	26.3
40	25.6
41	25
42	24.4
43	23.8
44	23.3
45	22.8
46	22.3
47	21.8
48	21.3
49	20.9
50	20.5
51	20.1
52	19.7
53	19.3
54	19
55	18.6
56	18.3
57	18
58	17.7
59	17.4
60	17.1
61	16.8
62	16.5
63	16.3
64	16
65	15.8
66	15.5
67	15.3
68	15.1
69	14.8
70	14.6
71	14.4
72	14.2
73	14
74	13.8
75	13.7
76	13.5
77	13.3
78	13.1
79	13
80	12.8
81	12.6
82	12.5
83	12.3
84	12.2
85	12
86	11.9
87	11.8
88	11.6
89	11.5
90	11.4
91	11.3
92	11.1
93	11
94	10.9
95	10.8
96	10.7
97	10.6
98	10.4
99	10.3
100	10.2
101	10.1
102	10
103	9.9
104	9.8
105	9.8
106	9.7
107	9.6
108	9.5
109	9.4
110	9.3
111	9.2
112	9.1
113	9.1
114	9
115	8.9
116	8.8
117	8.8
118	8.7
119	8.6
120	8.5
121	8.5
122	8.4
123	8.3
124	8.3
125	8.2
126	8.1
127	8.1
128	8
129	7.9
130	7.9
131	7.8
132	7.8
133	7.7
134	7.6
135	7.6
136	7.5
137	7.5
138	7.4
139	7.4
140	7.3
141	7.3
142	7.2
143	7.2
144	7.1
145	7.1
146	7
147	7
148	6.9
149	6.9
150	6.8
151	6.8
152	6.7
153	6.7
154	6.6
155	6.6
156	6.6
157	6.5
158	6.5
159	6.4
160	6.4
161	6.4
162	6.3
163	6.3
164	6.2
165	6.2
166	6.2
167	6.1
168	6.1
169	6.1
170	6
171	6
172	6
173	5.9
174	5.9
175	5.9
176	5.8
177	5.8
178	5.8
179	5.7
180	5.7
181	5.7
182	5.6
183	5.6
184	5.6
185	5.5
186	5.5
187	5.5
188	5.4
189	5.4
190	5.4
191	5.4
192	5.3
193	5.3
194	5.3
195	5.3
196	5.2
197	5.2
198	5.2
199	5.1
200	5.1
201	5.1
202	5.1
203	5
204	5
205	5
206	5
207	4.9
208	4.9
209	4.9
210	4.9
211	4.9
212	4.8
213	4.8
214	4.8
215	4.8
216	4.7
217	4.7
218	4.7
219	4.7
220	4.7
221	4.6
222	4.6
223	4.6
224	4.6
225	4.6
226	4.5
227	4.5
228	4.5
229	4.5
230	4.5
231	4.4
232	4.4
233	4.4
234	4.4
235	4.4
236	4.3
237	4.3
238	4.3
239	4.3
240	4.3
241	4.2
242	4.2
243	4.2
244	4.2
245	4.2
246	4.2
247	4.1
248	4.1
249	4.1
250	4.1
251	4.1
252	4.1
253	4
254	4
255	0
 */
